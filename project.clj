(defproject lein-jruby "0.1.2-SNAPSHOT"
  :description "Run JRuby commands from Leiningen."
  :dependencies [[org.jruby/jruby-complete "1.6.7"]
                 [org.clojure/tools.logging "0.3.1"]
                 [lancet "1.0.1"]]
  :eval-in-leiningen true)
