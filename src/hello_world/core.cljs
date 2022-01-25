(ns ^:figwheel-hooks hello-world.core
  (:require [com.stuartsierra.component :as component]
            [hello-world.components.stats :as stats]
            [hello-world.components.world :as world]
            [cljsjs.stats]))

(enable-console-print!)

(defn system []
  (component/system-map :stats (stats/map->STATS {})
                        :world (component/using (world/map->WORLD {})
                                                [:stats])))

(defonce S (atom (system)))

(defn ^:before-load teardown []
  (swap! S component/stop))

(defn ^:after-load startup []
  (swap! S component/start))
