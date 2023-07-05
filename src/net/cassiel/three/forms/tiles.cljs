(ns net.cassiel.three.forms.tiles
  "Manual assembly of a mesh via BufferGeometry.
   Lifted largely from https://github.com/mrdoob/three.js/blob/master/examples/webgl_buffergeometry.html"
  (:require [net.cassiel.three.geom :as geom]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 4 4 4)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0x808080
                                                                     :wireframe true})))

        geometry (js/THREE.BufferGeometry.)

        [ax ay az] [-2 -2 -2]
        [bx by bz] [-2 2 2]
        [cx cy cz] [2 -2 2]

        positions #js [ax ay az bx by bz cx cy cz]

        ;; flat face normals
        pA (js/THREE.Vector3. ax ay az)
        pB (js/THREE.Vector3. bx by bz)
        pC (js/THREE.Vector3. cx cy cz)

        cb (js/THREE.Vector3.)
        ab (js/THREE.Vector3.)

        _ (do (ocall cb :subVectors pC pB)
              (ocall ab :subVectors pA pB)
              (ocall cb :cross ab)
              (ocall cb :normalize))

        nx (oget cb :x)
        ny (oget cb :y)
        nz (oget cb :z)

        normals #js [nx ny nz nx ny nz nx ny nz]
        colours #js [1 0 0 0.5
                     1 1 1 0.5
                     1 1 1 0.5]

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

        mesh (js/THREE.Mesh. geometry material)
        ]

    (geom/group frame
                mesh
                (geom/shift [0 0 5] (js/THREE.DirectionalLight. 0xFFFFFF 1)))))
