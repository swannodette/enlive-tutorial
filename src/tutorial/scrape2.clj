(ns tutorial.scrape2
  (:use [net.cgrand.enlive-html :only [html-resource select text first-child]]))

(def *base-url* "http://news.ycombinator.com/")

(defn fetch-url [url]
  (html-resource (java.net.URL. url)))

(defn hn-headlines-and-points []
  (map text
       (select (fetch-url *base-url*)
               #{[:td.title :a] [:td.subtext first-child]})))

(defn headline-and-points-str [[h s]]
  (fn [[h s]] (str h " (" s ")")))

(defn print-headlines-and-points []
  (doseq [line (map headline-and-points-str
                    (partition 2 (hn-headlines-and-points)))]
    (println line)))
