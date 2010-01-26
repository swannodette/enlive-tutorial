(ns tutorial.template2
  (:use [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.java-utils :only [file]])
  (:use compojure))

(def dummy-context 
     {:links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

;; change this line to reflect your setup
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(defsnippet link-model (file *webdir* "template2.html")  [:ul#widget :> first-child]
  [[text href]] 
  [:a] (do-> 
        (content text) 
        (set-attr :href href)))

(deftemplate index* (file *webdir* "template2.html")
 [ctxt] 
 [:ul#links] (content (map link-model (:links ctxt)))) 

(defn index
  ([] (index* {}))
  ([ctxt] (index* ctxt)))

(defroutes example-routes
  (GET "/"
    (apply str (index)))
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