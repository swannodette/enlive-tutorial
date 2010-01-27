(ns tutorial.template2
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.java-utils :only [file]])
  (:use compojure))

(def dummy-context 
     {:links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

(html/defsnippet link-model "tutorial/template2.html"  [:ul#links :> html/first-child]
  [[text href]] 
  [:a] (html/do-> 
        (html/content text) 
        (html/set-attr :href href)))

(html/deftemplate index "tutorial/template2.html"
  [ctxt] 
  [:ul#links] (html/content (map link-model (:links ctxt))))

(defroutes example-routes
  (GET "/"
    (apply str (index dummy-context)))
  (ANY "*"
    [404 "Page Not Found"]))

;; ========================================
;; The App
;; ========================================

(def *app* (atom nil))

(defn start-app []
  (if (not (nil? @*app*))
    (stop @*app*))
  (reset! *app* (run-server {:port 8080}
                            "/*" (servlet example-routes))))

(defn stop-app []
  (stop @*app*))