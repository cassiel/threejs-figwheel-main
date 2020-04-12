(ns hello-world.content
  (:require [cljsjs.three]))

(defn content []
  (let [geometry (js/THREE.BoxGeometry. 1 1 1)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                        :wireframe false}))
        cube (js/THREE.Mesh. geometry material)
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        group (js/THREE.Group.)]

    (.set (.. light1 -position) 10 10 10)
    (.set (.. light2 -position) -10 10 10)

    (doto group
      (.add cube)
      (.add light1)
      (.add light2))

    group))
