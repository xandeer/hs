(ns hs.clipboard
  (:refer-clojure :exclude [slurp slip])
  (:import (java.awt.datatransfer DataFlavor Transferable StringSelection)
           (java.awt Toolkit))
  (:require [hiccup.core :as hiccup]))

(defn get-clipboard
  []
  (-> (Toolkit/getDefaultToolkit)
      (.getSystemClipboard)))

(defn slurp
  []
  (when-let [^Transferable clip-text (some-> (get-clipboard)
                                             (.getContents nil))]
    (when (.isDataFlavorSupported clip-text DataFlavor/stringFlavor)
      (->> clip-text
           (#(.getTransferData % DataFlavor/stringFlavor))
           (cast String)))))

(defn spit
  [s]
  (let [sel (StringSelection. s)]
    (some-> (get-clipboard)
            (.setContents sel sel))))

(defn html []
  (hiccup/html
   [:html
     [:head
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:meta {:charset "utf-8"}]
      [:link {:rel "stylesheet" :type "text/css" :href "/css/screen.css"}]
      [:link {:rel "stylesheet" :type "text/css" :href "/css/clipboard.css"}]]
    [:body
     [:div {:class "content"} (slurp)]]]))
