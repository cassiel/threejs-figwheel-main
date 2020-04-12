(ns hello-world.sculpture
  (:require [hello-world.geom :as geom]
            [cljsjs.three]))

(def CELL-PITCH 0.5)
(def BAR-WIDTH 0.15)
(def CELLS-X 5)
(def CELLS-Y 13)
(def CELLS-Z 8)

(defn vertical
  "Form of vertical (Y) bars, x across and z front-to-back."
  [material x y z]

  (let [strip-geom (js/THREE.BoxGeometry.
                      BAR-WIDTH
                      (+ (* CELL-PITCH y) BAR-WIDTH)
                      BAR-WIDTH)
        strip-row-fn (fn []
                       (->> (repeatedly (inc x)
                                        #(js/THREE.Mesh. strip-geom material))
                            (apply geom/group-spaced-by [CELL-PITCH 0 0])))]

    (->> (repeatedly (inc z) strip-row-fn)
         (apply geom/group-spaced-by [0 0 CELL-PITCH]))))

(defn form []
  (let [red (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFF0000
                                                   :wireframe false}))
        green (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00FF00
                                                     :wireframe false}))
        blue (js/THREE.MeshPhongMaterial. (clj->js {:color 0x0000FF
                                                    :wireframe false}))
        obj (vertical red CELLS-X CELLS-Y CELLS-Z)
        obj-rx (->> (vertical green CELLS-X CELLS-Z CELLS-Y)
                    (geom/rotate [(/ js/Math.PI 2) 0 0]))
        obj-rz (->> (vertical blue CELLS-Y CELLS-X CELLS-Z)
                    (geom/rotate [0 0 (/ js/Math.PI 2)]))
        #_ obj-z #_ (->> (vertical material CELLS-Z CELLS-X)
                   (geom/rotate [0 0 (/ js/Math.PI 2)]))
        dlight (js/THREE.DirectionalLight. 0xFFFFFF 1)
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        ]

    (geom/group obj
                obj-rx
                obj-rz
                #_ (js/THREE.Mesh. (js/THREE.SphereGeometry. 0.1 32 32)
                                material)
                #_ (geom/shift [0 0 10] light1)
                #_ (geom/shift [0 10 0] light2)
                #_ (geom/shift [10 0 0] light3)
                (geom/shift [10 0 10] dlight))))
