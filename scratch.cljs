(ns user
  (:require [net.cassiel.three.core :as core]
            [clojure.spec.alpha :as s]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]
            [net.cassiel.three.forms.handmade :as handmade]))

(-> (deref core/S)
    :content
    :world)

(first  [])

(def x 0.5)
(def y 0.1)
(def z 1.0)

(map (fn [i] [(* i x) (* i y) (* i z)]) (range 10))

(reduce + (range 10))

(reduce conj
        (range 10)
        #js [99 98 97])

(reduce (fn [coll v] (reduce conj coll (ocall v :toArray)))
        (range 10)
        [(js/THREE.Vector3. 80 81 82)
         (js/THREE.Vector3. 90 91 92)])

(reduce conj
        (range 10)
        (-> (js/THREE.Vector3. 1 2 3)
            (ocall :toArray)))

;; -----


(js/THREE.Color. "hsl(0, 0%, 0%)")
THREE.Color

js/THREE.REVISION

;; ---- Back/forth point iteration (normalised):

(defn foo [x] (+ x x))

(defn position [num-rows num-cols n]
  (let [x (int (/ n num-rows))
        y (mod n num-rows)
        y (if (odd? x) (- num-rows 1 y) y)
        ]
    [(/ x (dec num-cols)) (/ y (dec num-rows))]))

(map (partial position 4 5) (range 20))

(int (/ 3 3))
(mod 4 3)

(even? 1)

(flatten [1 [2 [3 4]]])

;; -----

(s/def ::test (s/coll-of number? :kind seq?))

(s/check-asserts true)
(s/assert ::test [1 2 3])

;; ---- Geometry generation

(defn triangles [p1s p2s]
  (let [p1s' (partition 2 1 p1s)
        p2s' (partition 2 1 p2s)]
    (->> (map (fn [[p1 p2] [p3 p4]]
                [[p1 p3 p4] [p1 p4 p2]])
              p1s' p2s')
         flatten
         (partition 3)
         (partition 3)))
  )

(defn triangles [ps qs]

  )

(handmade/triangle-strip [[0 0 0] [1 1 1] [2 2 2]]
                         [[7 7 7] [8 8 8] [9 9 9]])


(handmade/triangle-mesh [
                         [[0 0 0] [1 1 1] [2 2 2]]
                         [[4 4 4] [5 5 5] [6 6 6]]
                         [[7 7 7] [8 8 8] [9 9 9]]
                         ])

(handmade/build-triangles)

(first (partition 2 1 [
                       [[0 0 0] [1 1 1] [2 2 2]]
                       [[7 7 7] [8 8 8] [9 9 9]]
                       ]))


(defn scale [[f1 t1] [f2 t2] v]
  (+ f2 (/ (* (- v f1) (- t2 f2))
           (- t1 f1))))

(conj [2 3] 1)

(partition 3 1
           (interleave [1 2 3 4] [7 8 9 10]))

(defn foo [& {:keys [x y z]}]
  (str "X: " x " Y: " y " Z: " z))

(foo :x 1 :y 2 :z 3)

(foo {:x 1 :y 2 :z 3})

(rand)
(js/Math.cos js/Math.PI)

js/Math.PI
