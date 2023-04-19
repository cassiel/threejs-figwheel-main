(defproject threejs-figwheel-main "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.11.4"]
                                  [com.bhauman/figwheel-main "0.2.16"]
                                  ;; optional but recommended
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]

                                  [com.stuartsierra/component "1.0.0"]
                                  [net.cassiel/lifecycle "0.1.0-SNAPSHOT"]

                                  [cljsjs/stats "16.0-0"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]}}
  :plugins [[lein-ancient "1.0.0-RC3"]]
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
