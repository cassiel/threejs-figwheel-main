(ns hello-world.components.world
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [hello-world.cube :as cube]
            [hello-world.sculpture :as sculpture]
            [cljsjs.three]))

;; For easy Git branch-based switching between forms as we develop:
(def models {:cube      {:form               cube/form
                         :rotation-increment 0.01
                         :background         0x000000}
             :sculpture {:form               sculpture/form
                         :rotation-increment 0.001
                         :background         0x808080}})

(def model (:cube models))

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
                             content ((:form model))
                             ;; An "alive" flag to let us kill the animation
                             ;; refresh when we tear down:
                             RUNNING (atom true)]
                        (.setSize renderer (.-innerWidth js/window) (.-innerHeight js/window))
                        (.appendChild (.-body js/document) (.-domElement renderer))

                        (set! (.. scene -background) (js/THREE.Color. (:background model)))

                        (set! (.. camera -position -z) 5)
                        (.update controls)

                        ;; Camera control preferences:
                        (set! (.. controls -enableDamping) true)
                        (set! (.. controls -dampingFactor) 0.25)
                        (set! (.. controls -screenSpacePanning) false)

                        (.add scene content)

                        (letfn [(animate [n]
                                  (when @RUNNING (js/requestAnimationFrame
                                                  (partial animate (inc n))))

                                  #_ (let [r (:rotation-increment model)]
                                    (set! (.. content -rotation -x)
                                          (+ r (.. content -rotation -x)))
                                    (set! (.. content -rotation -y)
                                          (+ r (.. content -rotation -y))))

                                  #_ (set! (.. content -rotation -x) (/ n 100))

                                  (.update controls)
                                  (.render renderer scene camera)
                                  (when-let [stats (:stats stats)] (.update stats)))]
                          (animate 0))

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
