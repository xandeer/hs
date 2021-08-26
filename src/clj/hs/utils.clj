(ns hs.utils
  (:require
   [hs.config :refer [env]]
   [clojure.java.io :as io]
   [clojure.string :as cstr])
  (:import
   [java.net NetworkInterface]))

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

(defn root? [path]
  (or (cstr/blank? path) (= home path)))

(def ignores #{".DS_Store" ".git"})
(defn ls [folder]
  (let [path (expand-with-root folder)
        file (io/as-file path)
        children (->> (.listFiles file)
                      (filter #(not (contains? ignores (.getName %))))
                      (sort-by #(.isFile %))
                      (mapv
                       (fn [f]
                         {:href (str
                                 (if (.isFile f) "/file"
                                     "/folder")
                                 folder
                                 (when-not (root? folder) "/")
                                 (.getName f))
                          :name (str (.getName f) (when (.isDirectory f) "/"))})))]
    (if (or (cstr/blank? folder) (= folder home))
      children
      (into [{:href (let [parent (subs folder 0 (cstr/last-index-of folder "/"))]
                      (if (cstr/blank? parent) home
                          (str "/folder" parent)))
              :name "../"}] children))))

(defn save-file [tmp path]
  (io/copy (io/file tmp) (io/file (expand-with-root path))))

(defn get-host-address []
  (-> (->> (NetworkInterface/getNetworkInterfaces)
           enumeration-seq
           (map bean)
           (mapcat :interfaceAddresses)
           (map bean)
           (filter :broadcast)
           (filter #(= (.getClass (:address %)) java.net.Inet4Address)))
      (nth 0)
      (get :address)
      .getHostAddress))
