(ns hello-world.components.content
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljsjs.three]))

(defrecord CONTENT [world installed?]
  Object
  (toString [this] (str "CONTENT " (seq this)))

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [scene (:scene world)
                             geometry (js/THREE.BoxGeometry. 1 1 1)
                             material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                                             :wireframe false}))
                             cube (js/THREE.Mesh. geometry material)
                             light1 (js/THREE.PointLight. 0xFF0000 1 100)
                             light2 (js/THREE.PointLight. 0x00FF00 1 100)
                             group (js/THREE.Group.)
                             ]

                         (doto group
                           (.add cube)
                           (.add light1)
                           (.add light2))

                         (.add scene group)

                         (.set (.. light1 -position) 10 10 10)
                         (.set (.. light2 -position) -10 10 10)

                         (assoc this :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(assoc this :installed? false))))
