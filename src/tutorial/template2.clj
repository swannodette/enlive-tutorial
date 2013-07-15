(ns tutorial.template2
  (:use [net.cgrand.enlive-html
         :only [deftemplate defsnippet content clone-for
                nth-of-type first-child do-> set-attr sniptest at emit*]]
        [net.cgrand.moustache :only [app]]
        [tutorial.utils :only [run-server render render-to-response
                               page-not-found]]))

;; =============================================================================
;; Dummy Data
;; =============================================================================

(def ^:dynamic *dummy-context*
     {:title "Enlive Template2 Tutorial"
      :sections [{:title "Clojure"
                  :data [{:text "Macros"
                          :href "http://www.clojure.org/macros"}
                         {:text "Multimethods & Hierarchies"
                          :href "http://www.clojure.org/multimethods"}]}
                 {:title "Compojure"
                  :data [{:text "Requests"
                          :href "http://www.compojure.org/docs/requests"}
                         {:text "Middleware"
                          :href "http://www.compojure.org/docs/middleware"}]}
                 {:title"Clojars"
                  :data [{:text "Clutch"
                          :href "http://clojars.org/org.clojars.ato/clutch"}
                         {:text "JOGL2"
                          :href "http://clojars.org/jogl2"}]}
                 {:title "Enlive"
                  :data [{:text "Getting Started"
                          :href "http://wiki.github.com/cgrand/enlive/getting-started"}
                         {:text "Syntax"
                          :href "http://enlive.cgrand.net/syntax.html"}]}]})

(defn links [ctxt]
  (reduce concat (map :links (:sections ctxt))))

;; =============================================================================
;; Templates
;; =============================================================================

; we only want to select a model link
(def ^:dynamic *link-sel* [[:.content (nth-of-type 1)] :> first-child])

(defsnippet link-model "tutorial/template2.html" *link-sel*
  [{:keys [text href]}]
  [:a] (do->
        (content text)
        (set-attr :href href)))

; we only want to select the model h2 ul range
(def ^:dynamic *section-sel* {[:.title] [[:.content (nth-of-type 1)]]})

(defsnippet section-model "tutorial/template2.html" *section-sel*
  [{:keys [title data]} model]
  [:.title]   (content title)
  [:.content] (content (map model data)))

(deftemplate index "tutorial/template2.html"
  [{:keys [title sections]}]
  [:#title] (content title)
  [:body]   (content (map #(section-model % link-model) sections)))

(def routes
     (app
      [""]  (fn [req] (render-to-response (index *dummy-context*)))
      [&]   page-not-found))

;; ========================================
;; The App
;; ========================================

(defonce ^:dynamic  *server* (run-server routes))