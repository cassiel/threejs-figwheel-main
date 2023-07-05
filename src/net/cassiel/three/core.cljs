(ns ^:figwheel-hooks net.cassiel.three.core
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.three.components.stats :as stats]
            [net.cassiel.three.components.world :as world]
            [clojure.spec.alpha :as s]
            [cljsjs.stats]))

(enable-console-print!)
(s/check-asserts true)

(defn system []
  (component/system-map :stats (stats/map->STATS {})
                        :world (component/using (world/map->WORLD {})
                                                [:stats])))

(defonce S (atom (system)))

(defn ^:before-load teardown []
  (swap! S component/stop))

(defn ^:after-load startup []
  (swap! S component/start))
