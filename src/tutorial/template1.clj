(ns tutorial.template1
  (:use [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.java-utils :only [file]])
  (:use compojure))

;; change this line to reflect your setup
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(html/deftemplate index* (file *webdir* "template1.html")
  [ctxt]
  [:p#message] #(if-let [msg (:message ctxt)] msg %))

(defn index
  ([] (index* {}))
  ([ctxt] (index* ctxt)))

(defroutes example-routes
  (GET "/"
    (apply str (index)))
  (GET "/change/"
    (apply str (index {:message "We changed the message!"})))
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