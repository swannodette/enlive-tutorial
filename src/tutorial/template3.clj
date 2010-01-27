(ns tutorial.template3
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.java-utils :only [file]])
  (:use compojure))

;; =============================================================================
;; Top Level Defs
;; =============================================================================

;; change this line to reflect your setup
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(def *hits* (atom 0))

(defmacro block [sym]
  `(fn [n#] (if (nil? ~sym) n# ((substitute ~sym) n#))))

;; =============================================================================
;; The Templates Ma!
;; =============================================================================

(html/deftemplate base (file *webdir* "base.html")
  [{header :header, body :body, footer :footer :as ctxt}] 
  [:#header] (block header)
  [:#left]   (block left)
  [:#footer] (block footer))

(html/defsnippet link-model (file *webdir* "3col.html")  [:ol#links :> first-child]
  [[text href]] 
  [:a] (html/do->
        (html/content text) 
        (html/set-attr :href href)))

(html/defsnippet body (file *webdir* "3col.html") [:div#body]
  [{left :left, middle :middle, right :right :as context}]
  [:div#left]   (block left)
  [:div#middle] (block middle)
  [:div#right]  (block right))

;; =============================================================================
;; Pages
;; =============================================================================

(def pagea-context
     {:name "Page A"
      :time "Fun Time"
      :links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

(defn pagea [ctxt]
     (base {:header (header ctxt)
            :body (body ctxt)
            :footer (footer ctxt)}))

(def pageb-context
     {:time "Funner Time"
      :links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

(defn pageb [ctxt]
     (base {:body (body ctxt)}))

(defn index
  ([] (index* {}))
  ([ctxt] (index* ctxt)))

;; =============================================================================
;; Routes
;; =============================================================================

(defroutes example-routes
  (GET "/static/"
    (serve-file *webdir* "template3.html"))
  (GET "/a/"
    (apply str (pagea pagea-context)))
  (GET "/b/"
    (apply str (pagea pageb-context)))
  (ANY "*"
    [404 "Page Not Found"]))

;; =============================================================================
;; The App
;; =============================================================================

(def *app* (atom nil))

(defn start-app []
  (if (not (nil? @*app*))
    (stop @*app*))
  (reset! *app* (run-server {:port 8080}
                            "/*" (servlet example-routes))))

(defn stop-app []
  (stop @*app*))