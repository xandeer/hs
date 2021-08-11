(ns hs.routes.home
  (:require
   [hs.config :refer [env]]
   [hs.layout :as layout]
   [clojure.java.io :as io]
   [hs.middleware :as middleware]
   [clojure.string :as cstr]
   [reitit.ring :as ring]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page [request]
  (layout/render request "about.html"))

(defn expand-file [s]
  "Replace the first `~` to `(System/getProperty \"user.home\")`"
  (cond
    (cstr/blank? s) "./"
    (.startsWith s "~") (cstr/replace-first s "~" (System/getProperty "user.home"))
    :else s))

(def hello-prefix "/hello")
(def hello-dir "?dir=")
(def static "/static")

(defn root []
  (or (-> env :options :dir) (:dir env)))

(defn expand-root []
  (expand-file (root)))

(defn ls [folder]
  (let [path (str (expand-root) folder)
        file (io/as-file path)
        children (.listFiles file)]
    (mapv (fn [f]
            {:href (str (if (.isDirectory f) (str hello-prefix hello-dir)
                            static)
                        folder "/" (.getName f))
             :name (str (.getName f) (when (.isDirectory f) "/"))})
          children)))

(defn hello-page [request]
  (let [dir (:dir (:params request))
        items (ls (str dir))]
    (layout/render
     request "hello.html"
     {:items (if (or (cstr/blank? dir) (.equals (root) dir))
               items
               (into [{:href (str hello-prefix
                                  (let [parent (subs dir 0 (cstr/last-index-of dir "/"))]
                                    (when (not (cstr/blank? parent))
                                      (str hello-dir parent))))
                      :name "../"}]
                     items))
      :query dir})))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   [hello-prefix {:get hello-page}]
   ["/static/*" (ring/create-file-handler {:path static :root (expand-root)})]
   ["/about" {:get about-page}]])
