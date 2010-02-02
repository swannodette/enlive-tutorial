(ns tutorial.template2
  (:use [net.cgrand.enlive-html :only [selector deftemplate defsnippet content nth-of-type]])
  (:use net.cgrand.contrib.utils)
  (:use compojure))

;; =============================================================================
;; Dummy Data
;; =============================================================================

(def *dummy-context*
     {:title "Enlive Template2 Tutorial"
      :sections [{:title "Clojure"
                  :links [{:text "Macros"
                           :href "http://www.clojure.org/macros"}
                          {:text "Multimethods & Hierarchies"
                           :href "http://www.clojure.org/multimethods"}]}
                 {:title "Compojure"
                  :links [{:text "Requests"
                           :href "http://www.compojure.org/docs/requests"}
                          {:text "Middleware"
                           :href "http://www.compojure.org/docs/middleware"}]}
                 {:title"Clojars"
                  :links [{:text "Clutch"
                           :href "http://clojars.org/org.clojars.ato/clutch"}
                          {:text "JOGL2"
                           :href "http://clojars.org/jogl2"}]}
                 {:title "Enlive"
                  :links [{:text "Getting Started"
                           :href "http://wiki.github.com/cgrand/enlive/getting-started"}
                          {:text "Syntax"
                           :href "http://enlive.cgrand.net/syntax.html"}]}]})

;; =============================================================================
;; Templates
;; =============================================================================

(def *link-sel* (html/selector [[:ul.links (nth-of-type 1)] :> html/first-child]))

(html/defsnippet link-model "tutorial/template2.html" *link-sel*
  [{:keys [text href]}]
  [:a] (html/do-> 
        (html/content text) 
        (html/set-attr :href href)))

(def *section-sel* (selector [:body :> #{[:h2.section-title (nth-of-type 1)]
                                         [:ul.links (nth-of-type 1)]}]))

(defsnippet section-model "tutorial/template2.html" *section-sel*
  [{:keys [title links]}]
  [:h2] (content title)
  [:ul] (content (map link-model links)))

(html/deftemplate index "tutorial/template2.html"
  [{:keys [title sections]}]
  [:#title] (html/content title)
  [:body] (html/content (map section-model sections)))

(defroutes example-routes
  (GET "/"
       (render (index *dummy-context*)))
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
