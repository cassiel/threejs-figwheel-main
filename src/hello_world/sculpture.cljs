(ns hello-world.sculpture
  (:require [hello-world.geom :as geom]
            [cljsjs.three]))

(defn form []
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
