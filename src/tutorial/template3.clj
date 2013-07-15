(ns tutorial.template3
  (:require [net.cgrand.enlive-html :as html])
  (:use [net.cgrand.moustache :only [app]]
        [tutorial.utils
         :only [run-server render-to-response render-request
                maybe-content maybe-substitute page-not-found]]))

;; =============================================================================
;; The Templates Ma!
;; =============================================================================

(html/deftemplate base "tutorial/base.html"
  [{:keys [title header main footer]}]
  [:#title]  (maybe-content title)
  [:#header] (maybe-substitute header)
  [:#main]   (maybe-substitute main)
  [:#footer] (maybe-substitute footer))

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

(defn viewa []
  (base {:title "View A"
         :main (three-col {})}))

(defn viewb []
  (let [navl (nav1)
        navr (nav2)]
   (base {:title "View B"
          :main (three-col {:left  navl
                            :right navr})})))

(defn viewc
  ([] (viewc nil))
  ([action]
     (let [navs [(nav1) (nav2)]
           [navl navr] (if (= action "reverse") (reverse navs) navs)]
       (base {:title "View C"
              :main (three-col {:left  navl
                                :right navr})}))))

(defn index
  ([] (base {}))
  ([ctxt] (base ctxt)))

;; =============================================================================
;; Routes
;; =============================================================================

(def routes
     (app
      [""]          (render-request index)
      ["a"]         (render-request viewa)
      ["b"]         (render-request viewb)
      ["c" ]        (render-request viewc)
      ["c" action]  (render-request viewc action)
      [&]           page-not-found))

;; =============================================================================
;; The App
;; =============================================================================

(defonce ^:dynamic *server* (run-server routes))