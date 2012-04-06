(ns leiningen.jruby
  (:require [lancet.core :as lancet]
            [leiningen.classpath :as classpath])
  (:import [org.jruby Main]
           [org.apache.tools.ant.types Path]))

(defn task-props [project]
  {:classname "org.jruby.Main"})

(.addTaskDefinition lancet/ant-project "java" org.apache.tools.ant.taskdefs.Java)


(defn jruby
  "Run a JRuby command"
  [project & keys]
  (let [task (doto (lancet/instantiate-task lancet/ant-project "java"
                                            (task-props project)))]
    (doseq [k keys] (.setValue (.createArg task) k))
    (.execute task)))

(defn ensure-gems
  [project gems]
  (apply jruby (concat [project "-v" "maybe_install_gems"] gems)))

(defn ensure-gem
  [project gem]
  (apply ensure-gems [project [gem]]))

(defn rake
  [project]
  (apply ensure-gem [project "rake"]))