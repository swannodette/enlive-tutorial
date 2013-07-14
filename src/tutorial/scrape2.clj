(ns tutorial.scrape2
  (:require [net.cgrand.enlive-html :as html]))

(def *base-url* "https://news.ycombinator.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn hn-headlines-and-points []
  (map html/text
       (html/select (fetch-url *base-url*)
                    #{[:td.title :a] [:td.subtext html/first-child]})))

(defn print-headlines-and-points []
  (doseq [line (map (fn [[h s]] (str h " (" s ")"))
                    (partition 2 (hn-headlines-and-points)))]
    (println line)))
