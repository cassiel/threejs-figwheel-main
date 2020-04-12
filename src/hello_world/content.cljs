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

(defn content []
  (let [geometry (js/THREE.BoxGeometry. 1 1 1)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                        :wireframe false}))
        cubes (repeatedly 9 #(js/THREE.Mesh. geometry material))
        light1 (js/THREE.PointLight. 0xFF0000 1 100)
        light2 (js/THREE.PointLight. 0x00FF00 1 100)]

    (group (apply group-spaced-by [1 1 1] cubes)
           (shift [10 10 10] light1)
           (shift [-10 10 10] light2))))
