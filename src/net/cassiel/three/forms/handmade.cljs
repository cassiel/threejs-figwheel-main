(ns net.cassiel.three.forms.handmade
  "Manual assembly of a mesh via BufferGeometry.
   Lifted largely from https://github.com/mrdoob/three.js/blob/master/examples/webgl_buffergeometry.html
   except we just call .computeVertexNormals() rather than do that manually."
  (:require [net.cassiel.three.geom :as geom]
            [clojure.spec.alpha :as s]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(s/def ::positions (s/coll-of number? :kind seq?))
(s/def ::colours (s/coll-of number? :kind seq?))
(s/def ::buffers (s/keys :req-un [::positions ::colours]))

(s/def ::triple (s/coll-of number? :length 3))
(s/def ::triple-seq (s/coll-of ::triple))
(s/def ::triangle (s/coll-of ::triple :length 3))
(s/def ::triangle-seq (s/coll-of ::triangle))

(defn- scale [[f1 t1] [f2 t2] v]
  (+ f2 (/ (* (- v f1) (- t2 f2))
           (- t1 f1))))

(defn- v3ize [[x y z]] (js/THREE.Vector3. x y z))

(defn- add-face-to
  "Add a face to the buffers' positions, colours (defaulted). Note: buffer
   sequences are assembled using conj so will be reversed if we use sequences
   rather than vectors. pA/pB/pC are triples."
  [{:keys [positions colours]} [pA pB pC]]

  (s/assert ::triple pA)
  (s/assert ::triple pB)
  (s/assert ::triple pC)

  (let [[pA pB pC] (map v3ize [pA pB pC])
        positions' (reduce (fn [coll v] (reduce conj coll (ocall v :toArray)))
                           positions
                           [pA pB pC])

        colours' (reduce conj colours [1 1 1 0.5
                                       1 1 1 0.5
                                       1 1 1 0.5])]

    (s/assert ::buffers {:positions positions'
                         :colours colours'})
    )
  )

(defn- triangles [p1s p2s]
  (let [p1s' (partition 2 1 p1s)
        p2s' (partition 2 1 p2s)]
    (s/assert ::triangle-seq (->> (map (fn [[p1 p2] [p3 p4]]
                                         [[p1 p3 p4] [p1 p4 p2]])
                                       p1s' p2s')
                                  flatten       ; Brute force...
                                  (partition 3) ; Points.
                                  (partition 3) ; Triangles.
                                  ))))

(defn- position-xy
  "Simple back/forth iteration returning (x, y) for successive indices, -1/1 normalised."
  [num-rows num-cols n]
  (let [x (int (/ n num-rows))
        y (mod n num-rows)
        y (if (odd? x) (- num-rows 1 y) y)]
    [(scale [0 (dec num-cols)] [-1 1] x)
     (scale [0 (dec num-rows)] [-1 1] y)]))

(defn- mutation-xy
  "Test mutation according to normalised (positive) (x, y, z) location. Result is signed (dx, dy)."
  [x y z]
  [(* z (rand) 0.1)
   (* z (rand) 0.1)])

(def ^:private ROWS 10)
(def ^:private COLS 20)
(def ^:private LAYERS 10)

(defn- build-mesh [layer-index]
  (let [geometry (js/THREE.BufferGeometry.)

        low-z (scale [0 LAYERS] [-1 1] layer-index)
        high-z (scale [0 LAYERS] [-1 1] (inc layer-index))

        points1 (s/assert ::triple-seq (map (fn [n] (conj (position-xy ROWS COLS n) low-z))
                                            (range (* ROWS COLS))))

        points2 (s/assert ::triple-seq (map (fn [n] (conj (position-xy ROWS COLS n) high-z))
                                            (range (* ROWS COLS))))

        triangles (triangles points1 points2)

        {:keys [positions colours]} (reduce add-face-to
                                            { }
                                            triangles)

        [positions colours] (map reverse [positions colours])

        dispose (fn [ba]
                  (ocall ba :onUpload #(this-as this (oset! this :array nil))))

        material (js/THREE.MeshPhongMaterial. #js {:color        0xD5D5D5
                                                   :specular     0xFFFFFF
                                                   :shininess    250
                                                   :side         js/THREE.DoubleSide
                                                   :vertexColors true
                                                   :transparent  true
                                                   :wireframe    false})]
    (doto geometry
      (ocall :setAttribute "position" (dispose (js/THREE.Float32BufferAttribute. positions 3)))
      (ocall :computeVertexNormals)
      (ocall :setAttribute "color" (dispose (js/THREE.Float32BufferAttribute. colours 4)))
      (ocall :computeBoundingSphere))

    (js/THREE.Mesh. geometry material)))

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 2 2 2)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0xFF0000
                                                                     :wireframe true})))]

    (geom/group frame
                (build-mesh 0)
                (geom/shift [2 2 2] (js/THREE.DirectionalLight. 0xFFFFFF 1))
                (geom/shift [0 -2 -2] (js/THREE.DirectionalLight. 0xFFFF00 1))
                )))
