(ns hs.routes.home
  (:require
   [hs.layout :as layout]
   [clojure.java.io :as io]
   [hs.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn hello-page [request]
  (layout/render request "hello.html" {:hello "# Hello"}))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/hello" {:get hello-page}]
   ["/about" {:get about-page}]])
