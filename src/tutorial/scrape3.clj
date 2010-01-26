(ns tutorial.scrape3
  (:use [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.seq-utils :only [indexed]]))

(def *base-url* "http://nytimes.com/")

(def *story-selector*
     (html/selector [[:div.story
                      (but :.advertisement)
                      (but :.autosStory)
                      (but :.adCreative)]]))

(def *extract-selector*
     (html/selector #{[:h2 :a],
                      [:h3 :a],
                      [:ul.headlinesOnly :h5 :a],
                      [root :> :h6 :a],
                      [:.byline],
                      [:.summary]}))

(defn fetch-url [url]
  (html-resource (java.net.URL. url)))

(defn stories []
  (html/select (fetch-url *base-url*) *story-selector*))

(defn extract [node]
  (let [result (map html/text (html/select [node] *extract-selector*))]
    (zipmap [:title :byline :summary] result)))

(defn print-story [story]
  (println "--------------------------------------------------")
  (println (:title story))
  (println "\t" (or (:byline story) "No byline"))
  (println "\t" (or (:summary story) "No summary")))

(defn print-stories []
  (doseq [story (map extract (stories))]
    (print-story story)))

(comment
  (defn empty-indices [v]
    (filter (fn [[i s]] (nil? (seq s))) (indexed v)))
  )


