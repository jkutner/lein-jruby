(ns leiningen.test.jruby
  (:import [org.apache.tools.ant.types FileSet]
           java.io.File)
  (:use [leiningen.core :only (defproject read-project)]
        clojure.test leiningen.jruby leiningen.test.helper))

(refer-private 'leiningen.jruby)

(def project (read-project "sample/project.clj"))

; this is supposed to fail
(deftest test-rake-fail
  (jruby project "rake" "-f" "sample/Rakefile" "taskthatdoesntexist"))

(deftest test-rake
  (jruby project "rake" "-f" "sample/Rakefile" "simple"))

; not sure how to set the bundle file (its under sample/)
; (deftest test-rake
;   (jruby project "bundle" "install"))

(deftest test-jruby
  (jruby project "exec" "-e" "p(42)"))

(deftest test-jruby
  (jruby project "-e" "p(42)"))

(deftest test-jruby
  (jruby project "-v"))

(deftest test-any-starts-with
  (is (any-starts-with? "in" ["install"])))

(deftest test-any-starts-without
  (is (not (any-starts-with? "in" ["foobar"]))))

(deftest test-gem-dir
  (is (= ".lein-gems" gem-dir)))

(deftest test-rubygems-dir
  (is (= ".lein-gems/gems" rubygems-gem-path)))

; not really sure if i want to install a gem everytime
; (deftest test-gem-install
;   (gem project "install" "json"))

; need some bundler tests!