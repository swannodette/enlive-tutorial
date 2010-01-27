(ns tutorial.template1
  (:use [tutorial.utils :only [render]])
  (:use [net.cgrand.enlive-html :as html])
  (:use compojure))

(html/deftemplate index* "tutorial/template1.html"
  [ctxt]
  [:p#message] #(if-let [msg (:message ctxt)] msg %))

(defn index
  ([] (index* {}))
  ([ctxt] (index* ctxt)))

(defroutes example-routes
  (GET "/"
    (render (index)))
  (GET "/change/"
    (render (index {:message "We changed the message!"})))
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