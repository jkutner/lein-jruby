(ns leiningen.jruby
  (:use [clojure.java.io :only (file)])
  (:require [lancet.core :as lancet]
            [leiningen.classpath :as classpath]
            [clojure.string :as str])
  (:import [org.jruby Main]
           [org.apache.tools.ant.types Path]
           [org.apache.tools.ant ExitException]))

(def gem-dir ".lein-gems")

(def rubygems-dir (str gem-dir "/gems"))

(def gem-install-dir-arg 
  (format "-i%s" rubygems-dir))

(defn- task-props [project]
  {:classname "org.jruby.Main"})

(.addTaskDefinition lancet/ant-project "java" org.apache.tools.ant.taskdefs.Java)

(defn- jruby-exec
  [project & keys]
  (let [full-jruby-dir (file (:root project) "jruby")
        url-classpath (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))
        classpath (str/join java.io.File/pathSeparatorChar (map #(.getPath %) url-classpath))
        task (doto (lancet/instantiate-task lancet/ant-project "java"
                                              (task-props project))
                  (.setClasspath (Path. lancet/ant-project classpath)))]
      (doseq [k keys] (.setValue (.createArg task) k))
      (.setValue (.createArg task) (format "-I%s" (.getAbsolutePath full-jruby-dir)))
      (.setFailonerror task false)
      (.setFork task true)
      (if (.exists full-jruby-dir) (.setDir task full-jruby-dir))
      (.execute task)))

(defn- any-starts-with?
  [prefix strs]
  (some (fn [str] (.startsWith str prefix)) strs))

(defn- ensure-gem-dir 
  [project]
  (.mkdir (file (:root project) gem-dir)))

(defn- ensure-gems
  [project & gems]
  (apply ensure-gem-dir [project])
  (apply jruby-exec (concat [project "-S" "maybe_install_gems"] gems [gem-install-dir-arg])))

(defn- ensure-gem
  [project gem]
  (apply ensure-gems [project gem]))

(defn- rake
  [project & args]
    (apply ensure-gem [project "rake"])
    (apply jruby-exec (concat [project "-S" "rake"] args)))

(defn- bundle
  [project & args]
    (apply ensure-gem [project "bundler"])
  (if (or (empty? args) (= (first args) "install"))
    (apply jruby-exec (concat [project "-S" "bundle"] args ["--path" gem-dir]))
    (apply jruby-exec (concat [project "-S" "bundle"] args))))

(defn- gem
  [project & args]
  (apply ensure-gem-dir [project])
  (if (any-starts-with? (first args) ["install" "uninstall" "update"])
    (apply jruby-exec (concat [project "-S" "gem"] args [gem-install-dir-arg]))
    (apply jruby-exec (concat [project "-S" "gem"] args ))))

(defn jruby
  "Run a JRuby command"
  [project & keys]
  (case (first keys)
    "rake" (apply rake (cons project (rest keys)))
    "bundle" (apply bundle (cons project (rest keys)))
    "irb" (apply jruby-exec (concat [project "-S"] keys))
    "exec" (apply jruby-exec (cons project (rest keys)))
    "gem" (apply jruby-exec (cons project (rest keys)))
    "-S" (apply jruby-exec (cons project keys))
    "-v" (apply jruby-exec (cons project keys))
    "-e" (apply jruby-exec (cons project keys))))