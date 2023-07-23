(ns net.cassiel.three.forms.handmade
  "Manual assembly of a mesh via BufferGeometry.
   Lifted largely from https://github.com/mrdoob/three.js/blob/master/examples/webgl_buffergeometry.html
   except we just call .computeVertexNormals() rather than do that manually."
  (:require [net.cassiel.three.geom :as geom]
            [net.cassiel.three.geom-fns :as fns]
            [net.cassiel.three.utils :as u]
            [clojure.spec.alpha :as s]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(s/def ::positions (s/coll-of number? :kind seq?))
(s/def ::colours (s/coll-of number? :kind seq?))
(s/def ::buffers (s/keys :req-un [::positions ::colours]))

(s/def ::triple (s/coll-of number? :length 3))
(s/def ::triple-seq (s/coll-of ::triple))
(s/def ::triangle (s/coll-of ::triple :length 3))
(s/def ::triangle-seq (s/coll-of ::triangle))

(def ^:private ROWS 4)
(def ^:private COLS 4)
(def ^:private LAYERS 40)

(defn- position-xy
  "Simple back/forth iteration returning (x, y) for successive indices, -1/1 normalised."
  [n]
  (let [x (int (/ n ROWS))
        y (mod n ROWS)
        y (if (odd? x) (- ROWS 1 y) y)]
    [(u/scale [0 (dec COLS)] [-1 1] x)
     (u/scale [0 (dec ROWS)] [-1 1] y)]))

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

(defn triangle-strip [p1s p2s]
  ;; Alternate faces will be in reverse handedness - issue for translucency.
  (s/assert ::triangle-seq (partition 3 1 (interleave p1s p2s))))

(defn triangle-mesh [point-list-seq]
  (let [strips (map (partial apply triangle-strip) (partition 2 1 point-list-seq))]
    (s/assert ::triangle-seq (reduce concat strips))))

(defn- build-triangles []
  (let [num-points (* ROWS COLS)
        point-list-seq (map (fn [z-index] (map (fn [n]
                                               (let [[x y z] (conj (position-xy n) (u/scale [0 (dec LAYERS)] [-1 1] z-index))
                                                     [x' y'] (fns/main :x x
                                                                       :y y
                                                                       :z z
                                                                       :phase (u/scale [0 (dec num-points)] [-1 1] n))]
                                                 [x' y' z]))
                                               (range num-points)))
                            (range LAYERS))]
    (triangle-mesh point-list-seq)))

(defn- build-mesh []
  (let [geometry (js/THREE.BufferGeometry.)

        triangles (build-triangles)

        {:keys [positions colours]} (reduce add-face-to
                                            { }
                                            triangles)

        [positions colours] (map reverse [positions colours])

        dispose (fn [ba]
                  (ocall ba :onUpload #(this-as this (oset! this :array nil))))

        material (js/THREE.MeshPhongMaterial. #js {:color        0x000000
                                                   :specular     0xFFFFFF
                                                   :shininess    250
                                                   :side         js/THREE.DoubleSide
                                                   :vertexColors true
                                                   :transparent  true
                                                   :opacity      0.3
                                                   :wireframe    false})]
    (doto geometry
      (ocall :setAttribute "position" (dispose (js/THREE.Float32BufferAttribute. positions 3)))
      (ocall :computeVertexNormals)
      (ocall :setAttribute "color" (dispose (js/THREE.Float32BufferAttribute. colours 4)))
      (ocall :computeBoundingSphere))

    (let [wireframe (js/THREE.WireframeGeometry. geometry)
          line      (js/THREE.LineSegments. geometry)]
      (doto line
        (oset! :material.depthTest false)
        (oset! :material.opacity 1)
        (oset! :material.color 0xFFFFFF)
        (oset! :material.transparent false))

      (geom/group (js/THREE.Mesh. geometry material)
                  line))))

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 2 2 2)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0xFF0000
                                                                     :wireframe true})))]

    (geom/group frame
                (build-mesh)
                (geom/shift [2 2 2] (js/THREE.DirectionalLight. 0xFFFFFF 1))
                (geom/shift [0 0 0] (js/THREE.DirectionalLight. 0xFFFF00 1))
                (geom/shift [-2 -2 -2] (js/THREE.DirectionalLight. 0xFF8800 1))
                )))
