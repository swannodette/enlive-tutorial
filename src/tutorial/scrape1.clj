(ns tutorial.scrape1
  (:require [net.cgrand.enlive-html :as html]))

(def *base-url* "http://news.ycombinator.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn hn-headlines []
  (map html/text (html/select (fetch-url *base-url*) [:td.title :a])))

(defn hn-scores []
  (map html/text (html/select (fetch-url *base-url*) [:td.subtext first-child])))

(defn print-headlines-and-scores []
  (doseq [line (map #(str %1 " (" %2 ")") (hn-headlines) (hn-scores))]
    (println line)))