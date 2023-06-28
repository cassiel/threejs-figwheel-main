(ns user
  (:require [hello-world.core :as core]
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

;; -----


(js/THREE.Color. "hsl(0, 0%, 0%)")
THREE.Color

js/THREE.REVISION
