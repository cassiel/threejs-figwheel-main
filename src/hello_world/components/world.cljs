(ns hello-world.components.world
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [cljsjs.three]))

(defrecord WORLD [scene renderer stopper stats installed?]
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
                             controls (js/THREE.OrbitControls. camera (.-domElement renderer))
                             ;; An "alive" flag to let us kill the animation
                             ;; refresh when we tear down:
                             RUNNING (atom true)]
                        (.setSize renderer (.-innerWidth js/window) (.-innerHeight js/window))
                        (.appendChild (.-body js/document) (.-domElement renderer))

                        (set! (.. camera -position -z) 2)
                        (.update controls)

                        ;; Camera control preferences:
                        (set! (.. controls -enableDamping) true)
                        (set! (.. controls -dampingFactor) 0.25)
                        (set! (.. controls -screenSpacePanning) false)

                        (letfn [(animate []
                                  (when @RUNNING (js/requestAnimationFrame animate))

                                  ;; Assuming we only have one top-level group
                                  ;; (see components/content), rotate it. (We have
                                  ;; to wait for it to be injected first.)
                                  (when-let [obj (first (.-children scene))]
                                    (set! (.. obj -rotation -x)
                                          (+ 0.01 (.. obj -rotation -x)))
                                    (set! (.. obj -rotation -y)
                                          (+ 0.01 (.. obj -rotation -y))))

                                  (.update controls)
                                  (.render renderer scene camera)
                                  (when-let [stats (:stats stats)] (.update stats)))]
                          (animate))

                        (assoc this
                               :scene scene
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
                                :scene nil
                                :installed? false)))))
