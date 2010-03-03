(ns tutorial.template2
  (:use [net.cgrand.enlive-html
         :only [selector deftemplate defsnippet content clone-for
                nth-of-type first-child do-> set-attr sniptest at emit*]])
  (:use tutorial.utils)
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

(defn links [ctxt]
  (reduce concat (map :links (:sections ctxt))))

;; =============================================================================
;; Templates
;; =============================================================================

; we only want to select a model ink
(def *link-sel* (selector [[:ul (nth-of-type 1)] :> first-child]))

(defsnippet link-model "tutorial/template2.html" *link-sel*
  [{:keys [text href]}]
  [:a] (do->
        (content text)
        (set-attr :href href)))

; we only want to select the model h2 ul range
(def *section-sel* (selector {[:h2] [[:ul (nth-of-type 1)]]}))

(defsnippet section-model "tutorial/template2.html" *section-sel*
  [{:keys [title links]}]
  [:h2] (content title)
  [:ul] (content (map link-model links)))

(deftemplate index "tutorial/template2.html"
  [{:keys [title sections]}]
  [:#title] (content title)
  [:body] (content (map section-model sections)))

(deftemplate links-page "tutorial/links-page.html"
  [links]
  [:body] (content (map link-model links)))

(defroutes example-routes
  (GET "/"
       (render (index *dummy-context*)))
  (GET "/links"
       (render (links-page (links *dummy-context*))))
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
