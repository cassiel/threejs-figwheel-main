(ns net.cassiel.three.geom-fns
  "Functions and combinators for geometries."
  (:require [net.cassiel.three.utils :as u])
  )

(comment
  (defn interp-fn
    "Given a function with specified in and out range, create a scaled one.
   (In-range assumed same for x and y.)"
    [{:keys [f in-range out-range]}
     in-range'
     out-range']

    {:in-range in-range'
     :out-range out-range'
     :f (fn [x y]
          (->> (f (u/scale in-range' in-range x)
                  (u/scale in-range' in-range y))
               (u/scale out-range out-range')))}))

(defn- delta-to-abs-form [f]
  (fn [& {:keys [x y z phase]}]
    (let [[dx dy] (f :x x :y y :z z :phase phase)]
      [(+ x dx) (+ y dy)])))

(defn- z-crossfade [f1 f2]
  (fn [& {:keys [x y z phase]}]
    (let [[x1 y1] (f1 :x x :y y :z z :phase phase)
          [x2 y2] (f2 :x x :y y :z z :phase phase)]
      [(u/scale [-1 1] [x1 x2] z)
       (u/scale [-1 1] [y1 y2] z)])))

(defn- x-crossfade [f1 f2]
  (fn [& {:keys [x y z phase]}]
    (let [[y1 z1] (f1 :x x :y y :z z :phase phase)
          [y2 z2] (f2 :x x :y y :z z :phase phase)]
      [(u/scale [-1 1] [y1 y2] x)
       (u/scale [-1 1] [z1 z2] x)])))



(defn ident [& {:keys [x y]}] [x y])

;; General arguments:
;;      x, y, z: normalised signed positions in the rendering cube
;;      phase: normalised (-1.0..1.0) position along the rendering path
;; Output: signed normalised (x, y)

(def ^:private signed-rand #(u/scale [0 1] [-1 1] (rand)))

(defn disc [& {:keys [phase]}]
  (let [theta (u/scale [-1 1] [0 (* 2 js/Math.PI)] phase)]
    [(js/Math.sin theta)
     (js/Math.cos theta)]))

(defn line [& {:keys [phase]}]
  [phase phase])

(defn mutation-xy
  [& {:keys [x y z phase]}]
  [(* phase (signed-rand) 0.2)
   (* phase (signed-rand) 0.2)])

;; (def main (delta-to-abs-form mutation-xy))
(def main (z-crossfade (x-crossfade mutation-xy disc)
                       ident))
