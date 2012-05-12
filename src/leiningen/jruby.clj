(ns leiningen.jruby
  (:use [clojure.java.io :only (file)])
  (:require [lancet.core :as lancet]
            [leiningen.classpath :as classpath]
            [clojure.string :as str]
            [clojure.contrib.logging :as logger])
  (:import [org.jruby Main]
           [org.apache.tools.ant.types Path]
           [org.apache.tools.ant.types Environment$Variable]
           [org.apache.tools.ant ExitException]))

(def gem-dir ".lein-gems")

(def bundler-18-gem-path (str gem-dir "/jruby/1.8"))

(def bundler-19-gem-path (str gem-dir "/jruby/1.9"))

(def rubygems-gem-path (str gem-dir "/gems"))

(defn- gem-install-dir-arg 
  [project]
  (format "-i%s" (str (:root project) "/" rubygems-gem-path)))

(defn- gem-bundler-install-dir-arg 
  [project]
  ; TODO test 1.8/1.9
  (format "-i%s" (str (:root project) "/" bundler-18-gem-path)))

(defn- task-props [project]
  {:classname "org.jruby.Main"})

(.addTaskDefinition lancet/ant-project "java" org.apache.tools.ant.taskdefs.Java)

(defn- create-jruby-task
  [project keys]
  (let [full-jruby-dir (file (:root project) "src")        
        url-classpath (seq (.getURLs (java.lang.ClassLoader/getSystemClassLoader)))
        classpath (str/join java.io.File/pathSeparatorChar (map #(.getPath %) url-classpath))
        task (doto (lancet/instantiate-task lancet/ant-project "java"
                                              (task-props project))
                  (.setClasspath (Path. lancet/ant-project classpath)))]

      ; this should really add all source paths
      ;(.setValue (.createArg task) (format "-I%s" (.getAbsolutePath full-jruby-dir)))
      ;(.setValue (.createArg task) "-rubygems")

      (doseq [k keys] (.setValue (.createArg task) k))

      (.setFailonerror task false)
      (.setFork task (not (= "irb" (second keys))))

      ; i still don't get how it picks up the Gemfile and Rakefile with this set.. ?
      (if (.exists full-jruby-dir) (.setDir task full-jruby-dir))
      task))

(defn- set-gem-path
  [task gem-path]
  (let [envvar (new Environment$Variable)]
    ;(.setNewenvironment task true)
    (.setKey envvar "GEM_PATH") 
    (.setValue envvar (str gem-path))
    (.addEnv task envvar)))

(defn- set-gem-home
  [task gem-home]
  (let [envvar (new Environment$Variable)]
    ;(.setNewenvironment task true)
    (.setKey envvar "GEM_HOME") 
    (.setValue envvar (str gem-home))
    (.addEnv task envvar)))

(defn- set-path
  [task gem-dir]
  (let [envvar (new Environment$Variable)]
    (.setKey envvar "PATH") 
    (.setValue envvar (str gem-dir "/bin"))
    (.addEnv task envvar)))

(defn- jruby-exec
  [project & keys]
  (let [task (create-jruby-task project keys)]
    (logger/debug (str "jruby exec" keys))

    ; this may not be a good idea, but can't find another way to get the rubygems bin picked up
    ; another option might be to put it on the classpath. kind of a pain to do that and I'm lazy
    ; right now :P
    (apply set-path [task (str (:root project) "/" rubygems-gem-path)]) 

    (apply set-gem-home [task (str (:root project) "/" rubygems-gem-path)]) ;":" bundler-18-gem-path ":" bundler-19-gem-path)])

    (.execute task)))

(defn- jruby-bundle-exec
  [project & keys]
  (let [task (create-jruby-task project keys)
        ; once we can configure 1.8 v 1.9 we'll need to adjust this
        bundler-gem-path (str (:root project) "/" bundler-18-gem-path)] ; ":" "../" bundler-19-gem-path)])

    (logger/debug (str "bundle exec" keys))

    ; this may not be a good idea, but can't find another way to get the rubygems bin picked up
    ; another option might be to put it on the classpath. kind of a pain to do that and I'm lazy
    ; right now :P
    (apply set-path [task bundler-gem-path]) 

    (apply set-gem-home [task bundler-gem-path])
    (apply set-gem-path [task bundler-gem-path]) ;(str bundler-gem-path ":" (str (:root project) "/" rubygems-gem-path))]) 

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
  (apply jruby-exec (concat 
    [project "-S" "maybe_install_gems"] gems [(gem-install-dir-arg project)])))

(defn- ensure-bundler
  [project]
  (apply ensure-gem-dir [project])

  ;yea, not really bundle execing, but we need that stuff on the gem path
  (apply jruby-bundle-exec (concat 
    [project "-S" "maybe_install_gems"] ["bundler"] [(gem-bundler-install-dir-arg project)])))

(defn- ensure-gem
  [project gem]
  (apply ensure-gems [project gem]))

(defn- rake
  [project & args]
    (apply ensure-gem [project "rake"])
    (apply jruby-exec (concat [project "-S" "rake"] args)))

(defn- bundle
  [project & args]
  (apply ensure-bundler [project])
  (if (or (empty? args) (= (first args) "install"))
    (apply jruby-bundle-exec (concat [project "-S" "bundle"] args ["--path" gem-dir]))
    (if (= "exec" (first args))
      (apply jruby-bundle-exec (concat [project "-S"] (rest args)))
      (apply jruby-bundle-exec (concat [project "-S" "bundle"] args)))))

(defn- gem
  [project & args]
  (apply ensure-gem-dir [project])
  (if (any-starts-with? (first args) ["install" "uninstall" "update"])
    (apply jruby-exec (concat 
      [project "-S" "gem"] args [(gem-install-dir-arg project)]))
    (apply jruby-exec (concat 
      [project "-S" "gem"] args ))))

(defn jruby
  "Run a JRuby command"
  [project & keys]
  (case (first keys)
    "rake" (apply rake (cons project (rest keys)))
    "bundle" (apply bundle (cons project (rest keys)))
    "irb" (apply jruby-exec (concat [project "-S"] keys))
    ;"exec" (apply jruby-exec (cons project (rest keys)))
    "gem" (apply gem (cons project (rest keys)))
    "-S" (apply jruby-exec (cons project keys))
    "-v" (apply jruby-exec (cons project keys))
    "-e" (apply jruby-exec (cons project keys))))