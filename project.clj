(defproject cljs-dom-survey "0.0.1"
  :description "CLJS DOM Survey"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]
                 [domina "1.0.2"]
                 [prismatic/dommy "0.1.1"]
                 [jayq "2.5.0"]
                 [enfocus "2.0.2"]]

  :plugins [[lein-cljsbuild "1.0.0-alpha2"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "cljs_dom_survey"
              :source-paths ["src"]
              :compiler {
                :output-to "cljs_dom_survey.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
