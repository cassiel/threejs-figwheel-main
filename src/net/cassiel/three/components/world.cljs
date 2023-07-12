(ns net.cassiel.three.components.world
  (:require [com.stuartsierra.component :as component]
            [net.cassiel.lifecycle :refer [starting stopping]]
            [net.cassiel.three.forms.cube :as cube]
            [net.cassiel.three.forms.haüy :as haüy]
            [net.cassiel.three.forms.printed :as printed]
            [net.cassiel.three.forms.handmade :as handmade]
            [net.cassiel.three.forms.sculpture :as sculpture]
            [oops.core :refer [oget oget+ oset! oset!+ ocall oapply]]))

(defrecord WORLD [scene renderer stopper stats installed?]
  Object
  (toString [this] (str "WORLD " (seq this)))

  component/Lifecycle
  (start [this]
    (starting this
              :on installed?
              :action #(let [_ (js/console.log (str "[THREE v" js/THREE.REVISION "]"))
                             scene (js/THREE.Scene.)
                             camera (js/THREE.PerspectiveCamera. 75
                                                                 (/ (.-innerWidth js/window)
                                                                    (.-innerHeight js/window))
                                                                 0.1
                                                                 1000)
                             renderer (js/THREE.WebGLRenderer.)
                             controls (js/THREE.OrbitControls. camera (.-domElement renderer))
                             content (handmade/form)
                             ;; An "alive" flag to let us kill the animation
                             ;; refresh when we tear down:
                             RUNNING (atom true)]
                        (.setSize renderer (.-innerWidth js/window) (.-innerHeight js/window))
                        (.appendChild (.-body js/document) (.-domElement renderer))

                        (set! (.. scene -background) (js/THREE.Color. 0x808080))

                        (doto camera
                          (oset! :position.x 2)
                          (oset! :position.y 1.5)
                          (oset! :position.z 2))

                        ;; Camera control preferences:

                        (doto controls
                          (ocall :update)
                          (oset! :enableDamping true)
                          (oset! :dampingFactor 0.25)
                          (oset! :screenSpacePanning false))

                        (.add scene content)

                        (letfn [(animate [n]
                                  (when @RUNNING (js/requestAnimationFrame
                                                  (partial animate (inc n))))

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
