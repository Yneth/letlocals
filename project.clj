(defproject letlocals "1.0.0-SNAPSHOT"
  :description "letlocals - missing part of clojure"
  :url "https://github.com/Yneth/deflocals"

  :license {:name         "The MIT License"
            :url          "http://opensource.org/licenses/mit-license.php"
            :distribution :repo}
  :scm {:name "git"
        :url  "https://github.com/Yneth/letlocals"}

  :global-vars {*warn-on-reflection* false}

  :min-lein-version "2.0.0"

  :exclusions [org.clojure/clojure]
  :repl-options {:init-ns letlocals.core}
  :dependencies [[org.clojure/clojure "1.10.3"]]
  :profiles {:dev  {:repl-options {:init-ns letlocals.core}

                    :dependencies [[org.clojure/clojure "1.11.0"]]
                    :plugins      [[lein-ancient "0.7.0"]
                                   [jonase/eastwood "1.2.3"]
                                   [lein-kibit "0.1.8"]
                                   [lein-nvd "2.0.0"]]}
             :1.6  {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7  {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8  {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9  {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :1.10 {:dependencies [[org.clojure/clojure "1.10.3"]]}
             :1.11 {:dependencies [[org.clojure/clojure "1.11.0"]]}}
  :aliases {"all" ["with-profile" "dev,1.6:dev,1.7:dev,1.8:dev,1.9:dev,1.10:dev"]})
