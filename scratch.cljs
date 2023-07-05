(ns user
  (:require [net.cassiel.three.core :as core]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

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
