(ns hs.core
  (:require
    [hs.handler :as handler]
    [hs.nrepl :as nrepl]
    [hs.config :refer [env]]
    [hs.utils :refer [get-host-address]]
    [clojure.tools.cli :refer [parse-opts]]
    [clojure.tools.logging :as log]
    [org.httpkit.server :refer [run-server]]
    [mount.core :as mount])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
  (reify Thread$UncaughtExceptionHandler
    (uncaughtException [_ thread ex]
      (log/error {:what :uncaught-exception
                  :exception ex
                  :where (str "Uncaught exception on" (.getName thread))}))))

(def cli-options
  [["-d" "--dir DIRECTORY" "Directory"
    :parse-fn #(str %)]
   ["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(mount/defstate ^{:on-reload :noop} http-server
  :start
  (do
    (log/info "Starting on:"
              (str (get-host-address) ":"
                   (-> env
                       (update :port #(or (-> env :options :port) %))
                       (:port))))
    (run-server (handler/app)
                (-> env
                    (update :port #(or (-> env :options :port) %))
                    (update :thread #(or 3 %))
                    (select-keys [:thread :port]))))
  :stop
  (http-server :timeout 100))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info component "stopped"))
  (shutdown-agents))

(defn start-app [args]
  (doseq [component (-> args
                        (parse-opts cli-options)
                        mount/start-with-args
                        :started)]
    (log/info component "started"))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main [& args]
  (start-app args))
