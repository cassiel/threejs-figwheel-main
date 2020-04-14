(ns hello-world.haüy
  (:require [hello-world.geom :as geom]
            [cljsjs.three]))

(def base-geom (js/THREE.BoxGeometry. 0.5 0.5 0.5))
(defn rand-grey []
  (js/THREE.Color. (str "hsl(0, 0%, " (int (+ 60 (* (rand) 40))) "%)"))
                )
(defn material [] (js/THREE.MeshPhongMaterial. (clj->js {:color (rand-grey)
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
        structure (apply geom/group-spaced-by [0 0.5 0] layers)]
    (geom/group structure
                (geom/shift [0 0 0.5] (js/THREE.DirectionalLight. 0xFFFFFF 0.5)))))
