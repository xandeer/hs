(ns hs.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[hs started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[hs has shut down successfully]=-"))
   :middleware identity})
