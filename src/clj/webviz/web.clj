(ns webviz.web
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.string :as str])
  (:use [compojure.core]
        [ring.adapter.jetty]
        [ring.middleware.content-type :only [wrap-content-type]]
        [ring.middleware.file :only [wrap-file]]
        [ring.middleware.file-info :only [wrap-file-info]]
        [ring.middleware.stacktrace :only [wrap-stacktrace]]
        [ring.util.response :only [redirect]]
        [hiccup core element page]
        [hiccup.middleware :only [wrap-base-url]]))

(defn d3-page
  [title js body & {:keys [extra-js] :or {extra-js []}}]
  (html5
   [:head [:title title]
    (include-css "/css/nv.d3.css")
    (include-css "/css/style.css")]
   [:body (concat [body]
                  [(include-js "http://d3js.org/d3.v3.min.js")
                   (include-js "https://raw.github.com/novus/nvd3/master/nv.d3.min.js")]
                  (map include-js extra-js)
                  [(include-js "/js/goog/base.js")
                   (include-js "/js/webviz.js")
                   (javascript-tag "goog.require('webviz.barchart');")
                   (javascript-tag "goog.require('webviz.force');")
                   (javascript-tag "goog.require('webviz.histogram');")
                   (javascript-tag "goog.require('webviz.scatter');")
                   (javascript-tag "goog.require('webviz.core');")
                   (javascript-tag js)])]))

(defn force-layout-plot []
  (d3-page "Force-Directed Layout"
           "webviz.force.force_layout();"
           [:div#force.chart [:svg]]))

(defn hist-plot []
  (d3-page "Histogram"
           "webviz.histogram.histogram();"
           [:div#histogram.chart [:svg]]))

(defn scatter-charts []
  (d3-page "Scatter Chart"
           "webviz.scatter.scatter_plot();"
           [:div#scatter.chart [:svg]]))

(defn bar-chart []
  (d3-page "Bar Chart"
           "webviz.barchart.bar_chart();"
           [:div#barchart.chart [:svg]]))

(defn index-page []
  (html5
   [:head [:title "Web Charts"]]
   [:body [:h1 {:id "web-charts"} "Web Charts"]
    [:ol
     [:li [:a {:href "/data/census-race.json"}
               "2010 Census Race Data"]]
     [:li [:a {:href "/scatter"}
           "2010 Racial Scatter Plot"]]
     [:li [:a {:href "/barchart"}
           "Chick Weight Bar Chart"]]
     [:li [:a {:href "/histogram"}
           "Abalone Lengths Histogram"]]
     [:li [:a {:href "/force"}
               "2010 Census Racial Clusters"]]]
    (include-js "js/goog/base.js")
    (include-js "js/webviz.js")
    (javascript-tag "goog.require('webviz.core');")]))

(defroutes site-routes
  (GET "/" [] (index-page))
  (GET "/force" [] (force-layout-plot))
  (GET "/force/data.json" []
       (redirect "/data/clusters.json"))
  (GET "/histogram" [] (hist-plot))
  (GET "/histogram/data.json" []
       (redirect "/data/abalone.json"))
  (GET "/barchart" [] (bar-chart))
  (GET "/barchart/data.json" []
       (redirect "/data/chick-weight.json"))
  (GET "/scatter" [] (scatter-charts))
  (GET "/scatter/data.json" []
       (redirect "/data/census-race.json"))
  (route/resources "/")
  (route/not-found "Page not found"))

(def app
  (-> (handler/site site-routes)
      (wrap-file "resources")
      (wrap-file-info)
      (wrap-content-type)))
