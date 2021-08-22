(ns hs.env
  (:require
    [clojure.tools.logging :as log]
    [hs.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[hs started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[hs has shut down successfully]=-"))
   :middleware wrap-dev})
