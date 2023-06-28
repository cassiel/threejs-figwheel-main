(ns net.cassiel.three.components.stats
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]))

(defrecord STATS [stats installed?]
  Object
  (toString [this] (str "STATS " (seq this)))

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [stats (js/Stats.)
                             el (.-domElement stats)]
                         (set! (.. el -style -position) "absolute")
                         (set! (.. el -style -left) "10px")
                         (set! (.. el -style -top) "10px")
                         (.appendChild (.-body js/document) el)
                         (assoc this
                                :stats stats
                                :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do
                         (.removeChild (.-body js/document) (.-domElement stats))
                         (assoc this
                                :stats nil
                                :installed? false)))))
