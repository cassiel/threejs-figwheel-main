(ns ^:figwheel-hooks hello-world.core
  (:require [com.stuartsierra.component :as component]
            [hello-world.components.stats :as stats]
            [hello-world.components.world :as world]
            [hello-world.components.content :as content]
            [cljsjs.three]
            [cljsjs.stats]))

(enable-console-print!)

(defn system []
  (component/system-map :stats (stats/map->STATS {})
                        :world (component/using (world/map->WORLD {})
                                                [:stats])
                        :content (component/using (content/map->CONTENT {})
                                                  [:world])))

(defonce S (atom (system)))

(defn ^:before-load teardown []
  (swap! S component/stop))

(defn ^:after-load startup []
  (swap! S component/start))
