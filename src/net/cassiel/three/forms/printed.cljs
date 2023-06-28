(ns net.cassiel.three.forms.printed
  (:require [net.cassiel.three.geom :as geom]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(defn form []
  (let [frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 4 4 4)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color     0x404040
                                                                     :wireframe true})))

        geometry  (js/THREE.BufferGeometry.)
        material (js/THREE.LineBasicMaterial. #js {:vertexColors true})
        positions (repeatedly 1000 (fn [] (repeatedly 3 #(- (* (rand) 4) 2))))
        colours   (repeatedly 1000 (fn []
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
