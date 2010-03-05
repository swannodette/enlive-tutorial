(ns tutorial.scrape3
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.seq-utils :only [indexed]]
        [clojure.contrib.str-utils :only [re-sub re-gsub]]))

(def *base-url* "http://nytimes.com/")

;; TODO: exclude bottom page links
;; some of the byline stuff seems way off
;; strip new line characters
;; MAYBE: unicode chars

(def *story-selector*
     (html/selector [[:div.story
                      (html/but :.advertisement)
                      (html/but :.autosStory)
                      (html/but :.adCreative)]]))

(def *extract-selector*
     (html/selector #{[:h2 :a],
                      [:h3 :a],
                      [:ul.headlinesOnly :h5 :a],
                      [html/root :> :h6 :a],
                      [:.byline],
                      [:.summary]}))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn stories []
  (html/select (fetch-url *base-url*) *story-selector*))

(defn extract [node]
  (let [result (map html/text (html/select [node] *extract-selector*))]
    (zipmap [:title :byline :summary] (map #(re-gsub #"\n" "" %) result))))

(defn print-story [story]
  (println)
  (println (:title story))
  (println "\t" (or (:byline story) "No byline"))
  (println "\t" (or (:summary story) "No summary")))

(defn print-stories []
  (doseq [story (map extract (stories))]
    (print-story story)))

