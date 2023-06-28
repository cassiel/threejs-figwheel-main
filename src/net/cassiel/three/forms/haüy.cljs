(ns net.cassiel.three.forms.haüy
  (:require [net.cassiel.three.geom :as geom]))

(def base-geom (js/THREE.BoxGeometry. 0.5 0.5 0.5))
(defn rand-grey []
  (js/THREE.Color. (str "hsl(0, 0%, " (int (+ 90 (* (rand) 10))) "%)"))
                )
(defn material [] (js/THREE.MeshBasicMaterial. (clj->js {:color (rand-grey)
                                                         :wireframe false})))

(defn- triangle-numbers [n]
  (concat (range n)
          (reverse (range (dec n)))))

(defn- half-tri [n]
  (map #(inc (* % 2))
       (range n)))

(defn- odd-triangle-numbers [n]
  (concat (half-tri n)
          (reverse (half-tri (dec n)))))

(defn- layer [n]
  (letfn [(row-fn [n]
            (->> (repeatedly n #(js/THREE.Mesh. base-geom (material)))
                 (apply geom/group-spaced-by [0.5 0 0])))]
    (let [rows (map row-fn (odd-triangle-numbers n))]
      (apply geom/group-spaced-by [0 0 0.5] rows))))

(defn form []
  (let [layers (map layer (triangle-numbers 7))
        structure (apply geom/group-spaced-by [0 0.5 0] layers)
        frame (js/THREE.Mesh. (js/THREE.BoxGeometry. 4 4 4)
                              (js/THREE.MeshBasicMaterial. (clj->js {:color 0xFF0000
                                                                     :wireframe true})))
        p4 (/ js/Math.PI 4)
        v (js/THREE.Vector3. 1 1 1)]
    (.normalize v)
    #_ (-> structure (.rotateOnWorldAxis v (/ js/Math.PI p4)))
    (geom/group (geom/rotate [p4 0 p4] structure) ;; Approximation
                frame
                (geom/shift [0 0 5] (js/THREE.DirectionalLight. 0xFFFFFF 1)))))
