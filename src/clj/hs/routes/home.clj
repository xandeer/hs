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

(defn about-page [request]
  (layout/render request "about.html"))

(defn expand-file [s]
  "Replace the first `~` to `(System/getProperty \"user.home\")`"
  (cond
    (cstr/blank? s) "./"
    (.startsWith s "~") (cstr/replace-first s "~" (System/getProperty "user.home"))
    :else s))

(def home "/")
(def static "/static")

(defn root []
  (or (-> env :options :dir) (:dir env)))

(defn expand-root []
  (expand-file (root)))

(defn expand-with-root [child]
  (str (expand-root) child))

(defn ls [folder]
  (let [path (expand-with-root folder)
        file (io/as-file path)
        children (sort-by #(.isFile %) (.listFiles file))]
    (mapv (fn [f]
            {:href (str (if (.isDirectory f) "?dir=" static)
                        folder "/" (.getName f))
             :name (str (.getName f) (when (.isDirectory f) "/"))})
          children)))

(defn home-page [request]
  (log/info "req: " request)
  (let [dir (:dir (:params request))
        items (ls dir)]
    (layout/render
     request "home.html"
     {:items (if (or (cstr/blank? dir) (.equals home dir))
               items
               (into [{:href (let [parent (subs dir 0 (cstr/last-index-of dir "/"))]
                               (if (cstr/blank? parent) home
                                   (str "?dir=" parent)))
                      :name "../"}]
                     items))
      :query dir})))

(defn save-file [req]
  (let [tmp (:tempfile (:file (:params req)))
        path (expand-with-root (cstr/replace-first (:uri req) static ""))]
    (log/info "tmp:" tmp "cus:" path)
    (do
      (io/copy tmp (io/file path))
      {:status 200, :body "ok"})))

(defn home-routes []
  [""
   {:middleware [
                 ;; middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/static/*" {:get (ring/create-file-handler {:path static :root (expand-root)})
                 :post save-file}]
   ["/about" {:get about-page}]])
