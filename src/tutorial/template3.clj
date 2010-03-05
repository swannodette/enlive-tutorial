(ns tutorial.template3
  (:require [net.cgrand.enlive-html :as html])
  (:use tutorial.utils)
  (:use [clojure.contrib.duck-streams :only [pwd]])
  (:use compojure))

;; =============================================================================
;; Top Level Defs
;; =============================================================================

(def *webdir* (str (pwd) "/src/tutorial/"))

;; =============================================================================
;; The Templates Ma!
;; =============================================================================

(html/deftemplate base "tutorial/base.html"
  [{:keys [title header main footer]}]
  [:#title]      (maybe-content title)
  [:#header]     (maybe-substitute header)
  [:#main]       (maybe-substitute main)
  [:#footer]     (maybe-substitute footer))

(html/defsnippet three-col "tutorial/3col.html" [:div#main]
  [{:keys [left middle right]}]
  [:div#left]   (maybe-substitute left)
  [:div#middle] (maybe-substitute middle)
  [:div#right]  (maybe-substitute right))

(html/defsnippet nav1 "tutorial/navs.html" [:div#nav1] [])
(html/defsnippet nav2 "tutorial/navs.html" [:div#nav2] [])
(html/defsnippet nav3 "tutorial/navs.html" [:div#nav3] [])

;; =============================================================================
;; Pages
;; =============================================================================

(defn viewa [params session]
  (base {:title "View A"
         :main (three-col {})}))

(defn viewb [params session]
  (let [navl (nav1)
        navr (nav2)]
   (base {:title "View B"
          :main (three-col {:left  navl
                            :right navr})})))

(defn viewc [params session]
  (let [navs [(nav1) (nav2)]
        [navl navr] (if (= (:action params) "reverse") (reverse navs) navs)]
    (base {:title "View C"
           :main (three-col {:left  navl
                             :right navr})})))

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
