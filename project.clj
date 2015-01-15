(defproject webviz "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2665"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.1"]
                 [hiccup "1.0.5"]]

  :node-dependencies [[source-map-support "0.2.8"]]

  :plugins [[lein-cljsbuild "1.0.4"]
            [lein-npm "0.4.0"]
            [lein-ring "0.8.13"]]
  :ring {:handler webviz.web/app}
  :source-paths ["src/clj" "target/classes"]
  :resource-paths ["resources"]
  :clean-targets ["out/webviz" "resources/js/webviz.js" "resources/js/webviz.min.js"]

  :cljsbuild {
    :builds [{:id "dev"
              :source-paths ["src/cljs"]
              :compiler {
                :output-to "resources/js/webviz.js"
                :output-dir "resources/js"
                :optimizations :none
                :pretty-print true
                :cache-analysis true                
                :source-map true}}
             {:id "release"
              :source-paths ["src/cljs"]
              :compiler {
                :output-to "resources/js/webviz.min.js"
                :pretty-print false              
                :optimizations :advanced}}]})
