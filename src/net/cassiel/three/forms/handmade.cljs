(ns net.cassiel.three.forms.handmade
  "Manual assembly of a mesh via BufferGeometry.
   Lifted largely from https://github.com/mrdoob/three.js/blob/master/examples/webgl_buffergeometry.html"
  (:require [net.cassiel.three.geom :as geom]
            [clojure.spec.alpha :as s]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(s/def ::positions (s/coll-of number? :kind seq?))
(s/def ::normals (s/coll-of number? :kind seq?))
(s/def ::colours (s/coll-of number? :kind seq?))
(s/def ::buffers (s/keys :req [::positions ::normals ::colours]))

(defn- v3ize [[x y z]] (js/THREE.Vector3. x y z))

(defn- add-face-to
  "Add a face to the buffers' positions, normals, colours (defaulted). Note: buffer
   sequences are assembled using conj so will be reversed if we use sequences
   rather than vectors. pA/pB/pC are triples."
  [{:keys [positions normals colours]} [pA pB pC]]

  (let [[pA pB pC] (map v3ize [pA pB pC])
        positions' (reduce (fn [coll v] (reduce conj coll (ocall v :toArray)))
                           positions
                           [pA pB pC])

        cb (js/THREE.Vector3.)
        ab (js/THREE.Vector3.)

        _ (do (ocall cb :subVectors pC pB) ; cb = pB->pC.
              (ocall ab :subVectors pA pB) ; ab = pB->pA.
              (ocall cb :cross ab)         ; TODO: to me that seems left-handed.
              (ocall cb :normalize))

        nx (oget cb :x)
        ny (oget cb :y)
        nz (oget cb :z)

        normals' (reduce conj normals (take 9 (cycle [nx ny nz])))
        colours' (reduce conj colours [1 0 0 0.5
                                       0 1 0 0.5
                                       0 0 1 0.5])]

    (s/assert ::buffers {:positions positions'
                         :normals normals'
                         :colours colours'})
    )
  )

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 4 4 4)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0x808080
                                                                     :wireframe true})))

        geometry (js/THREE.BufferGeometry.)

        {:keys [positions normals colours]} (reduce add-face-to
                                                    { }
                                                    [[[-2 -2 -2] [-2 2 2] [2 -2 2]]
                                                     [[2 2 2] [2 -2 -2] [-2 2 -2]]])

        [positions normals colours] (map reverse [positions normals colours])

        dispose (fn [ba]
                  (ocall ba :onUpload #(this-as this (oset! this :array nil))))

        _ (doto geometry
            (ocall :setAttribute "position" (dispose (js/THREE.Float32BufferAttribute. positions 3)))
            (ocall :setAttribute "normal" (dispose (js/THREE.Float32BufferAttribute. normals 3)))
            (ocall :setAttribute "color" (dispose (js/THREE.Float32BufferAttribute. colours 4)))
            (ocall :computeBoundingSphere)
            )

        material (js/THREE.MeshPhongMaterial. #js {:color        0xD5D5D5
                                                   :specular     0xFFFFFF
                                                   :shininess    250
                                                   :side         js/THREE.DoubleSide
                                                   :vertexColors true
                                                   :transparent  true
                                                   :wireframe    false})

        mesh (js/THREE.Mesh. geometry material)]

    (geom/group frame
                mesh
                (geom/shift [0 0 5] (js/THREE.DirectionalLight. 0xFFFFFF 1))
                (geom/shift [0 0 -5] (js/THREE.DirectionalLight. 0xFFFF00 1))
                )))
