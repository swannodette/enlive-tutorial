(ns tutorial.scrape3
  (:use [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.seq-utils :only [indexed]]))

(def *base-url* "http://nytimes.com/")

(def *nytimes-selector*
     (html/selector #{[:h2 :a],
                      [:h3 :a],
                      [:ul.headlinesOnly :h5 :a],
                      [root :> :h6 :a],
                      [:.byline],
                      [:.summary]}))

(defn fetch-url [url]
  (html-resource (java.net.URL. url)))

(defn stories []
  (html/select (fetch-url *base-url*) [:div.story]))

(defn extract [node]
  (let [result (map html/text (html/select [node] *nytimes-selector*))]
    (zipmap [:title :byline :summary] result)))

(defn empty-indices [v]
  (filter (fn [[i s]] (nil? (seq s))) (indexed v)))

