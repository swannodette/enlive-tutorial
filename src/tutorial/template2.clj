(ns tutorial.template2
  (:use [tutorial.utils :only [render]])
  (:require [net.cgrand.enlive-html :as html])
  (:import [java.util Calendar])
  (:use compojure))

(defn now-str []
  (str (.getTime (Calendar/getInstance))))

(def dummy-context 
     {:title "Enlive Template2 Tutorial"
      :links [["Clojure" "http://www.clojure.org"]
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
  [:#date]    (html/content (:date ctxt))
  [:#title]   (html/content (:title ctxt))
  [:ul#links] (html/content (map link-model (:links ctxt))))

(defroutes example-routes
  (GET "/"
       (render (index (assoc dummy-context :date (now-str)))))
  (ANY "*"
       [404 "Page Not Found"]))

;; ========================================
;; The App
;; ========================================

(defonce *app* (atom nil))

(defn start-app []
  (if (not (nil? @*app*))
    (stop @*app*))
  (reset! *app* (run-server {:port 8080}
                            "/*" (servlet example-routes))))

(defn stop-app []
  (stop @*app*))
