(ns tutorial.scrape2
  (:use [net.cgrand.enlive-html :as html]))

(def *base-url* "http://news.ycombinator.com/")

(defn fetch-url [url]
  (html-resource (java.net.URL. url)))

(defn hn-headlines-and-scores []
  (map html/text
       (html/select (fetch-url *base-url*)
                    #{[:td.title :a] [:td.subtext first-child]})))

(defn print-headlines-and-scores []
  (doseq [line (map (fn [[h s]] (str h " (" s ")"))
                    (partition 2 (hn-headlines-and-scores)))]
    (println line)))
