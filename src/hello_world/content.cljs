(ns hello-world.content
  (:require [cljsjs.three]))

(defn- group [& objs]
  (let [g (js/THREE.Group.)]
    (doseq [x objs] (.add g x))
    g))

(defn- shift
  "Shift an object non-destructively by wrapping it in a singleton group."
  [[x y z] obj]
  (let [g (group obj)]
    (.set (.. g -position) x y z)
    g))

(defn- group-spaced-by
  "Group objects after shifting them to space them out according to [x y z]."
  [[x y z] & objs]
  (let [;; Overall range of spacing:
        ranges (map #(* % (dec (count objs))) [x y z])
        ;; Overall shift to re-centre:
        overall-shift (map #(- (/ % 2)) ranges)
        ;; Sequence of offsets for individual objects:
        offsets (map (fn [i] [(* i x) (* i y) (* i z)])
                     (range))
        shifted-objs (map shift offsets objs)
        ]
    ;; Centre the result:
    (shift overall-shift
           (apply group shifted-objs))))

(defn cube-matrix []
  (let [geometry (js/THREE.BoxGeometry. 1 1 1)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                        :wireframe false}))
        cube-row-fn (fn []
                      (->> (repeatedly 9 #(js/THREE.Mesh. geometry material))
                           (apply group-spaced-by [1.5 0 0])))
        cube-sheet-fn (fn []
                        (->> (repeatedly 9 cube-row-fn)
                             (apply group-spaced-by [0 1.5 0])))
        cube-matrix-fn (fn []
                         (->> (repeatedly 9 cube-sheet-fn)
                              (apply group-spaced-by [0 0 1.5])))
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        ]

    (group (cube-matrix-fn)
           (shift [0 0 10] light1)
           (shift [0 10 0] light2)
           (shift [10 0 0] light3))))

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
                            (apply group-spaced-by [CELL-PITCH 0 0])))
        strip-set-fn (fn []
                       (->> (repeatedly (inc CELLS-Y) strip-row-fn)
                            (apply group-spaced-by [0 CELL-PITCH 0])))
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)
        light3 (js/THREE.PointLight. 0x0000FF 1 100)
        ]

    (group (strip-set-fn)
           (shift [0 0 10] light1)
           (shift [0 10 0] light2)
           (shift [10 0 0] light3))))
