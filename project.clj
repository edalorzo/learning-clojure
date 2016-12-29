(defproject learn-clojure "0.1.0-SNAPSHOT"
  :description "Learning Clojure Project"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot learn.core
  :target-path "target/%s"
  :profiles {
             :uberjar {:aot :all}
             :user {:plugins [[venantius/ultra "0.5.0"]]}
             })
