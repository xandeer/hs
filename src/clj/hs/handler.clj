(ns hs.handler
  (:require
    [hs.env :refer [defaults]]
    [hs.utils :as utils]
    [clojure.string :as cstr]
    [compojure.core :refer :all]
    [compojure.route :as route]
    [clojure.tools.logging :as log]
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.multipart-params :refer [wrap-multipart-params]]
    [mount.core :as mount]
    [hiccup.core :as hiccup]))

(defn ll [path]
  (hiccup/html
   [:html
    [:head
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:meta {:charset "utf-8"}]
     [:link {:rel "stylesheet" :type "text/css" :href "/css/screen.css"}]]
    [:body
     [:h1 {:class "title"}
      (str "Dir: " path)]
     [:input#upload {:type "file"}]
     [:ul
      (for  [i (utils/ls path)]
        [:li
         [:a {:href (:href i)}
          (:name i)]])]
     [:script {:type "text/javascript" :src "/js/upload.js"}]]]))

(mount/defstate app-routes
  :start
  (routes
   (route/files "/file/" {:root (utils/expand-root)})
   (GET "/folder/*" req
        (let [uri (:uri req)
              path (cstr/replace-first uri "/folder" "")]
          (log/info "Path : " path)
          (ll path)))
   (GET "/" [] (ll "/"))
   (POST "/folder/*" req
         (let [tmp (:path (bean (get-in req [:params "file" :tempfile])))
               path (-> req (:uri) (cstr/replace-first "/folder" ""))]
           (log/info "Upload file save to" path)
           (utils/save-file tmp path)
           {:status 200, :body "ok"}))
   (route/resources "")
   (route/not-found "<h4>Not Found</h4>")))

(defn app []
  (log/info "Root:" (utils/expand-root))
  (-> #'app-routes
      (wrap-params)
      (wrap-multipart-params)))
