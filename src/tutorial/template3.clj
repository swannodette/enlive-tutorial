(ns tutorial.template3
  (:require [net.cgrand.enlive-html :as html])
  (:use tutorial.utils)
  (:use compojure))

;; =============================================================================
;; Top Level Defs
;; =============================================================================

;; change this line to reflect your setup
(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(def *hits* (atom 0))

;; =============================================================================
;; The Templates Ma!
;; =============================================================================

(html/deftemplate base "tutorial/base.html"
  [{title :title, header :header, main :main, footer :footer :as ctxt}]
  [:#title]      (maybe-content title)
  [:#header]     (maybe-substitute header)
  [:#main]       (maybe-substitute main)
  [:#footer]     (maybe-substitute footer))

(html/defsnippet link-model "tutorial/3col.html" [:ol#links :> html/first-child]
  [[text href]] 
  [:a] (html/do->
        (html/content text) 
        (html/set-attr :href href)))

(html/defsnippet three-col "tutorial/3col.html" [:div#main]
  [{left :left, middle :middle, right :right :as ctxt}]
  [:div#left]   (maybe-substitute left)
  [:div#middle] (maybe-substitute middle)
  [:div#right]  (maybe-substitute right))

(html/defsnippet nav1 "tutorial/navs.html" [:div#nav1]
  [{count :count :as ctxt}]
  [:.count] (html/content
             (str (parse-int count) " " (pluralize "thing" (parse-int count)))))

(html/defsnippet nav2 "tutorial/navs.html" [:div#nav2] [])

(html/defsnippet nav3 "tutorial/navs.html" [:div#nav3] [])

;; =============================================================================
;; Pages
;; =============================================================================

(defn viewa [params session]
  (base {:title "View A"
         :main (three-col {})}))

(defn viewb [params session]
  (let [nav1 (nav1 {:count (or (:count params) 0)})
        nav2 (nav2)]
   (base {:title "View B"
          :main (three-col {:left nav1
                            :right nav2})})))

(defn viewc [params session]
  (let [navs [(nav1 {:count (or (:count params) 0)})
              (nav2)]
        navs (if (= (:action params) "reverse") (reverse navs) navs)
        [nav1 nav2] navs]
    (base {:title "View C"
           :main (three-col {:left nav1
                             :right nav2})})))

(defn index
  ([] (base {}))
  ([ctxt] (base ctxt)))

;; =============================================================================
;; Routes
;; =============================================================================

(defroutes app-routes
  ;; app routes
  (GET "/"
       (render (index)))
  (GET "/a/"
       (render (viewa params session)))
  (GET "/b/"
       (render (viewb params session)))
  (GET "/b/:count"
       (render (viewb params session)))
  (GET "/c/"
       (render (viewc params session)))
  (GET "/c/:action"
       (render (viewc params session)))

  ;; static files
  (GET "/base.html"
       (serve-file *webdir* "base.html"))
  (GET "/3col.html"
       (serve-file *webdir* "3col.html"))
  (GET "/navs.html"
       (serve-file *webdir* "navs.html"))
  (GET "*/main.css"
       (serve-file *webdir* "main.css"))

  (ANY "*"
       [404 "Page Not Found"]))

;; =============================================================================
;; The App
;; =============================================================================

(defonce *app* (atom nil))

(defn start-app []
  (if (not (nil? @*app*))
    (stop @*app*))
  (reset! *app* (run-server {:port 8080}
                            "/*" (servlet app-routes))))

(defn stop-app []
  (stop @*app*))
