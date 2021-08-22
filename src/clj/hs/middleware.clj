(ns hs.middleware
  (:require
    [hs.config :refer [env]]
    [hs.env :refer [defaults]]
    [clojure.tools.logging :as log]
    [ring.middleware.anti-forgery :refer [wrap-anti-forgery]]
    [ring.middleware.flash :refer [wrap-flash]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]])
  )

(defn wrap-internal-error [handler]
  (fn [req]
    (try
      (handler req)
      (catch Throwable t
        (log/error t (.getMessage t))
        ;; (error-page
         {:status 500
          :title "Something very bad has happened!"
          :message "We've dispatched a team of highly trained gnomes to take care of the problem."}
         ;; )
        ))))

(defn wrap-csrf [handler]
  (wrap-anti-forgery
    handler
    {:error-response
     ;; (error-page
       {:status 403
        :title "Invalid anti-forgery token"}
     ;; )
    }))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-flash
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)
            (dissoc :session)))
      wrap-internal-error))
