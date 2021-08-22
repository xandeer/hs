(ns hs.handler
  (:require
    [hs.middleware :as middleware]
    [hs.env :refer [defaults]]
    [hs.utils :as utils]
    [compojure.core :refer :all]
    [compojure.route :refer [files]]
    [clojure.tools.logging :as log]
    [ring.middleware.defaults :refer :all]
    [mount.core :as mount]
    [hiccup.core :as hiccup]))

(defn ll [path]
  (hiccup/html
   [:html
    [:body
     [:h1 {:class "title"}
      (str "Dir: " path)]
     [:ul
      (for  [i (utils/ls path)]
        [:li
         [:a {:href (:href i)}
          (:name i)]])]
     ]]))

(mount/defstate app-routes
  :start
  (routes
   (files "/file/" {:root (utils/expand-root)})
   (GET "/folder/:path" [path]
        (log/info "Path : " path)
        (ll (str "/" path)))
   (GET "/" [] (ll ""))))

(defn app []
  (log/info "Root:" (utils/expand-root))
  (wrap-defaults #'app-routes site-defaults))
