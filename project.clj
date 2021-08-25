(defproject hs "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "https://github.com/xandeer/hs"

  :dependencies [[ch.qos.logback/logback-classic "1.2.3"]
                 [compojure "1.6.2"]
                 [cprop "0.1.17"]
                 [expound "0.8.9"]
                 [hiccup "1.0.5"]
                 [http-kit "2.5.3"]
                 ;; [metosin/reitit "0.5.13"]
                 [mount "0.1.16"]
                 [nrepl "0.8.3"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ring "1.9.4"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot hs.core

  :plugins []

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "hs.jar"
             :source-paths ["env/prod/clj" ]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:jvm-opts ["-Dconf=dev-config.edn" ]
                  :dependencies [[pjstadig/humane-test-output "0.11.0"]
                                 [prone "2021-04-23"]
                                 [ring/ring-devel "1.9.3"]
                                 ;; [ring/ring-mock "0.4.0"]
                                 ]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                 ;; [jonase/eastwood "0.3.5"]
                                 [cider/cider-nrepl "0.26.0"]]

                  :source-paths ["env/dev/clj" ]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 120000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]
                  }
   :project/test {:jvm-opts ["-Dconf=test-config.edn" ]
                  :resource-paths ["env/test/resources"] }
   :profiles/dev {}
   :profiles/test {}})
