(ns tutorial.template3
  (:use [net.cgrand.enlive-html])
  (:use [clojure.contrib.java-utils :only [file]])
  (:use compojure))

;; change this line to reflect your setup
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(def *hits* (atom 0))

(defmacro block [sym]
  `(fn [n#] (if (nil? ~sym) n# ((substitute ~sym) n#))))

(deftemplate layout (file *webdir* "template3.html")
  [{header :header body :body footer :footer :as ctxt}] 
  [:#header] (block header)
  [:#body]   (block body)
  [:#footer] (block footer))

(defsnippet header (file *webdir* "inherit.html") [:div#header]
  [{name :name :as ctxt}]
  [:span.name] (content name))

(defsnippet link-model (file *webdir* "inherit.html")  [:ol#hnlinks :> first-child]
  [[text href]] 
  [:a] (do->
        (content text) 
        (set-attr :href href)))

(defsnippet body (file *webdir* "inherit.html") [:div#body]
  [{time :time links :links :as context}]
  [:span.time] (content time)
  [:#hnlinks] (content (map link-model links)))

(defsnippet footer (file *webdir* "inherit.html") [:div#footer]
  [ctxt]
  [:span.hits] (content (str @*hits*)))

(def pagea-context
     {:name "Page A"
      :time "Fun Time"
      :links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

(defn pagea [ctxt]
     (layout {:header (header ctxt)
              :body (body ctxt)
              :footer (footer ctxt)}))

(def pageb-context
     {:time "Funner Time"
      :links [["Clojure" "http://www.clojure.org"]
              ["Compojure" "http://www.compojure.org"]
              ["Clojars" "http://www.clojars.org"]
              ["Enlive" "http://github.com/cgrand/enlive"]]})

(defn pageb [ctxt]
     (layout {:body (body ctxt)}))

(defn index
  ([] (index* {}))
  ([ctxt] (index* ctxt)))

(defroutes example-routes
  (GET "/static/"
    (serve-file *webdir* "template3.html"))
  (GET "/a/"
    (apply str (pagea pagea-context)))
  (GET "/b/"
    (apply str (pagea pageb-context)))
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