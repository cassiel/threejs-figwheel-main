(ns net.cassiel.three.cube
  (:require [net.cassiel.three.geom :as geom]))

(defn form []
  (let [geometry (js/THREE.BoxGeometry. 1 1 1)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                        :wireframe false}))
        cube-row-fn (fn []
                      (->> (repeatedly 9 #(js/THREE.Mesh. geometry material))
                           (apply geom/group-spaced-by [1.5 0 0])))
        cube-sheet-fn (fn []
                        (->> (repeatedly 9 cube-row-fn)
                             (apply geom/group-spaced-by [0 1.5 0])))
        cube-matrix-fn (fn []
                         (->> (repeatedly 9 cube-sheet-fn)
                              (apply geom/group-spaced-by [0 0 1.5])))
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        ]

    (geom/group (cube-matrix-fn)
                (geom/shift [0 0 10] light1)
                (geom/shift [0 10 0] light2)
                (geom/shift [10 0 0] light3))))

(defn sculpture []
  (let [CELL-PITCH 0.5
        BAR-WIDTH 0.02
        CELLS-X 5
        CELLS-Y 8
        CELLS-Z 13

        vertical-strip-geom (js/THREE.BoxGeometry.
                             BAR-WIDTH
                             BAR-WIDTH
                             (* CELL-PITCH CELLS-Z))
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                        :wireframe false}))
        strip-row-fn (fn []
                       (->> (repeatedly (inc CELLS-X)
                                        #(js/THREE.Mesh. vertical-strip-geom material))
                            (apply geom/group-spaced-by [CELL-PITCH 0 0])))
        strip-set-fn (fn []
                       (->> (repeatedly (inc CELLS-Y) strip-row-fn)
                            (apply geom/group-spaced-by [0 CELL-PITCH 0])))
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        ]

    (geom/group (strip-set-fn)
                (geom/shift [0 0 10] light1)
                (geom/shift [0 10 0] light2)
                (geom/shift [10 0 0] light3))))
