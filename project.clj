(defproject threejs-figwheel-main "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.11.1"]]
  :profiles {:dev {:dependencies [[org.clojure/clojurescript "1.11.60"]
                                  [com.bhauman/figwheel-main "0.2.18"]
                                  ;; optional but recommended
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]

                                  [com.stuartsierra/component "1.1.0"]
                                  [net.cassiel/lifecycle "0.1.0-SNAPSHOT"]

                                  [cljsjs/stats "16.0-0"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target"]}}
  :plugins [[lein-cljsbuild "1.1.8"]
            ;; These seem pretty equivalent:
            [lein-ancient "1.0.0-RC3"]
            [com.github.liquidz/antq "RELEASE"]]
  :cljsbuild {:builds [{:source-paths ["src"]
                        :compiler {:output-to "resources/public/js/_COMPILED.js"
                                   :optimizations :whitespace}}]}
  :aliases {"fig" ["trampoline" "run" "-m" "figwheel.main"]})
