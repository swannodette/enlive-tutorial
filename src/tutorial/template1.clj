(ns tutorial.template1
  (:require [net.cgrand.enlive-html :as html])
  (:use tutorial.utils)
  (:use [ring.middleware.reload :only [wrap-reload]]
        [ring.adapter.jetty :only [run-jetty]]
        [net.cgrand.moustache :only [app delegate]]))

(html/deftemplate index "tutorial/template1.html"
  [ctxt]
  [:p#message] (html/content (:message ctxt)))

;; ========================================
;; The App
;; ========================================

(def template1
  (app
   [""] index (render (index {}))
   ["change"] (render (index {:message "We changed the message!"}))
   [&]        [404 "Page Not Found"]))

(defonce *server* (run-jetty #'template1 {:port 8080 :join? false}))


