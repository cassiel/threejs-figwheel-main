(ns net.cassiel.three.forms.printed
  (:require [net.cassiel.three.geom :as geom]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(defn- scale [[f1 t1] [f2 t2] v]
  (+ f2 (/ (* (- v f1) (- t2 f2))
           (- t1 f1))))

(defn- position-xy
  "Simple back/forth iteration returning (x, y) for successive indices, -1/1 normalised."
  [num-rows num-cols n]
  (let [x (int (/ n num-rows))
        y (mod n num-rows)
        y (if (odd? x) (- num-rows 1 y) y)]
    [(-> (/ x (dec num-cols)) (* 2) (- 1))
     (-> (/ y (dec num-rows)) (* 2) (- 1))]))

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 4 4 4)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0x404040
                                                                     :wireframe true})))

        geometry  (js/THREE.BufferGeometry.)
        material  (js/THREE.LineBasicMaterial. #js {:vertexColors true})

        rows 10
        cols 20
        layers 10

        positions (map
                   (fn [z]
                     (map (fn [n] (let [[x y] (position-xy rows cols n)] [x y (scale [0 (dec layers)] [-1 1] z)]))
                          (range (* rows cols))))
                   (range layers))

        colours   (repeatedly 2000 (fn []
                                    (repeatedly 3 rand)
                                    #_ (if (> (rand) 0.5) [1 1 0] [0 0 1])
                                    #_ (repeatedly 3 #(if (> (rand) 0.5) 1 0))))
        ]
    (doto geometry
      (ocall :setAttribute "position" (js/THREE.Float32BufferAttribute. (-> positions flatten clj->js) 3))
      (ocall :setAttribute "color" (js/THREE.Float32BufferAttribute. (-> colours flatten clj->js) 3)))

    (geom/group frame
                ;; (Lighting not needed for wireframes/lines:)
                (js/THREE.Line. geometry material)
                (geom/shift [0 0 5] (js/THREE.DirectionalLight. 0xFFFFFF 1)))))
