(ns user
  (:require [hello-world.core :as core]))

(-> (deref core/S)
    :content
    :world)

(first  [])
