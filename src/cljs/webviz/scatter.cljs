(ns webviz.scatter
  (:require [webviz.core :as webviz]))

(defn sum-values [key-fn coll]
  (reduce
   (fn [m [k vs]] (assoc m k (webviz/sum-by key-fn vs)))
   {}
   coll))

(defn sum-data-fields [json]
  (let [by-state (group-by #(.-state_name %) json)
        white-by-state (sum-values #(.-white %) by-state)
        afam-by-state (sum-values #(.-black %) by-state)
        total-by-state (sum-values #(.-total %) by-state)]
    (map #(hash-map :state %
                    :white (white-by-state %)
                    :black (afam-by-state %)
                    :total (total-by-state %))
         (keys by-state))))

(defn ->nv [item]
  (let [{:keys [white black]} item]
    (webviz.core/Point. (/ white 1000) (/ black 1000) 1)))

(defn ->nv-data [key-name data]
  (->> data
       sum-data-fields
       (map ->nv)
       (apply array)
       (webviz.core/Group. key-name)
       (array)))

(defn make-chart []
  (let [c (-> (.scatterChart js/nv.models)
              (.showDistX true)
              (.showDistY true)
              (.useVoronoi false)
              (.color (.. js/d3 -scale category10 range)))]
    (.tickFormat (.-xAxis c) (.format js/d3 "d"))
    (.tickFormat (.-yAxis c) (.format js/d3 "d"))
    c))

(defn ^:export scatter-plot []
  (webviz.core/create-chart "/scatter/data.json"
                            "#scatter svg"
                            make-chart
                            (partial ->nv-data "Racial Data")
                            :x-label "Population, whites, by thousands"
                            :y-label "Population, African-Americans, by thousands"
                            :transition true))
