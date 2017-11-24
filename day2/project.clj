(defproject bathsecurity "0.1.0-SNAPSHOT"
  :description "Advent of Code - Day 2 - Bathroom Security"
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot bathsecurity.console
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
