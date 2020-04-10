(ns hello-world.components.world
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljsjs.three]))

(defrecord WORLD [renderer stopper stats installed?]
  Object
  (toString [this] (str "WORLD " (seq this)))

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [scene (js/THREE.Scene.)
                             camera (js/THREE.PerspectiveCamera. 75
                                                                 (/ (.-innerWidth js/window)
                                                                    (.-innerHeight js/window))
                                                                 0.1
                                                                 1000)
                             renderer (js/THREE.WebGLRenderer.)
                             geometry (js/THREE.BoxGeometry. 1 1 1)
                             material (js/THREE.MeshPhongMaterial. (clj->js {:color 0xFFFFFF
                                                                             :wireframe false}))
                             cube (js/THREE.Mesh. geometry material)
                             light1 (js/THREE.PointLight. 0xFF0000 1 100)
                             light2 (js/THREE.PointLight. 0x00FF00 1 100)
                             ;; An "alive" flag to let us kill the animation
                             ;; refresh when we tear down:
                             RUNNING (atom true)]
                        (.setSize renderer (.-innerWidth js/window) (.-innerHeight js/window))
                        (.appendChild (.-body js/document) (.-domElement renderer))

                        (doto scene
                          (.add cube)
                          (.add light1)
                          (.add light2))

                        (set! (.. camera -position -z) 2)
                        (.set (.. light1 -position) 10 10 10)
                        (.set (.. light2 -position) -10 10 10)

                        (letfn [(animate []
                                  (when @RUNNING (js/requestAnimationFrame animate))
                                  (set! (.. cube -rotation -x)
                                        (+ 0.01 (.. cube -rotation -x)))
                                  (set! (.. cube -rotation -y)
                                        (+ 0.01 (.. cube -rotation -y)))
                                  (.render renderer scene camera)
                                  (when-let [stats (:stats stats)] (.update stats)))]
                          (animate))

                        (assoc this
                               :renderer renderer
                               :stopper (fn [] (reset! RUNNING false))
                               :installed? true))))

  (stop [this]
    (stopping this
              :on installed?
              :action #(do
                         (stopper)
                         (.removeChild (.-body js/document) (.-domElement renderer))
                         (assoc this
                                :stopper nil
                                :renderer nil
                                :installed? false)))))
