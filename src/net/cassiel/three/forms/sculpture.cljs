(ns net.cassiel.three.forms.sculpture
  (:require [net.cassiel.three.geom :as geom]))

(def CELL-PITCH 0.5)
(def BAR-WIDTH 0.05)
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

(defn rand-colour []
  (js/THREE.Color. (str "hsl(" (* (rand) 360) ", 100%, 50%)")))

(def light-geometry (js/THREE.SphereGeometry. 0.1 32 32))
(defn light-material [c] (js/THREE.MeshPhongMaterial. (clj->js {:color c
                                                                :wireframe false})))

(defn light-matrix
  "Light source generation. We can't draw too many lights in a scene, so we
   fudge by making most of them inert and black."
  []
  (let [light-source-fn #(let [light? (> (rand) 0.98)
                               c (if light?
                                   #_ (js/THREE.Color. (rand) (rand) (rand))
                                   (rand-colour)
                                   (js/THREE.Color. 0x000000))
                               marker (js/THREE.Mesh. light-geometry
                                                      (light-material c))]
                           (if light?
                             (geom/group marker
                                         (js/THREE.PointLight. c 0.5 0.001 5))
                             marker))
        light-strip-x-fn #(->> (repeatedly CELLS-X light-source-fn)
                               (apply geom/group-spaced-by [CELL-PITCH 0 0]))
        light-plane-z-fn #(->> (repeatedly CELLS-Z light-strip-x-fn)
                               (apply geom/group-spaced-by [0 0 CELL-PITCH]))
        ]
    (->> (repeatedly CELLS-Y light-plane-z-fn)
         (apply geom/group-spaced-by [0 CELL-PITCH 0]))))

(defn form []
  (let [red (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFF8080
                                                   :wireframe false}))
        green (js/THREE.MeshPhongMaterial. (clj->js {:color 0x80FF80
                                                     :wireframe false}))
        blue (js/THREE.MeshPhongMaterial. (clj->js {:color 0x8080FF
                                                    :wireframe false}))
        dark (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                    :wireframe false}))
        obj (->> (vertical dark CELLS-X CELLS-Y CELLS-Z)
                 (geom/shift [(/ BAR-WIDTH 2) 0 0]))
        obj-rx (->> (vertical dark CELLS-X CELLS-Z CELLS-Y)
                    (geom/rotate [(/ js/Math.PI 2) 0 0])
                    (geom/shift [(- (/ BAR-WIDTH 2)) 0 0]))
        obj-rz (->> (vertical dark CELLS-Y CELLS-X CELLS-Z)
                    (geom/rotate [0 0 (/ js/Math.PI 2)])
                    (geom/shift [0 BAR-WIDTH BAR-WIDTH]))
        #_ obj-z #_ (->> (vertical material CELLS-Z CELLS-X)
                   (geom/rotate [0 0 (/ js/Math.PI 2)]))
        dlight (js/THREE.DirectionalLight. 0xFFFFFF 1)
        light1 (geom/shift [5 0 0] (js/THREE.PointLight. 0xFFFFFF 1 100))
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        mini-light-1 (js/THREE.PointLight. 0xFF0000 1 1 2)
        mini-light-2 (js/THREE.PointLight. 0xFFFF00 1 1 2)
        ]

    (geom/group obj
                obj-rx
                obj-rz
                #_ (js/THREE.Mesh. (js/THREE.SphereGeometry. 0.1 32 32)
                                material)
                #_ (geom/shift [0 2 0] mini-light-1)
                #_ (geom/shift [0 -2 0] mini-light-2)
                (light-matrix)
                #_ (geom/shift [0 10 0] light2)
                #_ (geom/shift [10 0 0] light3)
                #_ (geom/shift [10 0 10] dlight))))
