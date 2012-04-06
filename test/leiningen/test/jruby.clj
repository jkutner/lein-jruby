(ns leiningen.test.jruby
  (:import [org.apache.tools.ant.types FileSet]
           java.io.File)
  (:use [leiningen.core :only (defproject read-project)]
        clojure.test leiningen.jruby leiningen.test.helper))

(refer-private 'leiningen.jruby)

(def project (read-project "sample/project.clj"))


(deftest test-rake
  (rake project))

(deftest test-jruby
  (jruby project "-e" "p(100)"))