(ns leiningen.jruby
  (:require [lancet.core :as lancet]
            [leiningen.classpath :as classpath])
  (:import [org.jruby Main]
           [org.apache.tools.ant.types Path]
           [org.apache.tools.ant ExitException]))

(defn task-props [project]
  {:classname "org.jruby.Main"})

(.addTaskDefinition lancet/ant-project "java" org.apache.tools.ant.taskdefs.Java)

(defn jruby-exec
  [project & keys]
  (let [task (doto (lancet/instantiate-task lancet/ant-project "java"
                                              (task-props project)))]
      (doseq [k keys] (.setValue (.createArg task) k))
      (.setFailonerror task false)
      (try (.execute task) (catch ExitException println "Task failed!"))))

(defn ensure-gems
  [project & gems]
  (apply jruby-exec (concat [project "-S" "maybe_install_gems"] gems)))

(defn ensure-gem
  [project gem]
  (apply ensure-gems [project gem]))

(defn rake
  [project & args]
    (apply ensure-gem [project "rake"])
    (apply jruby-exec (concat [project "-S" "rake"] args)))

(defn bundle
  [project & args]
    (apply ensure-gem [project "bundler"])
    (apply jruby-exec (concat [project "-S" "bundle"] args)))

(defn jruby
  "Run a JRuby command"
  [project & keys]
  (case (first keys)
    "rake" (apply rake (cons project (rest keys)))
    "bundle" (apply bundle (cons project (rest keys)))
    "irb" (apply jruby-exec (concat [project "-S"] keys))
    "-S" (apply jruby-exec (cons project keys))
    "-e" (apply jruby-exec (cons project keys))))