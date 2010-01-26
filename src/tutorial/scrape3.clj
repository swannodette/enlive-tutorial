(ns tutorial.scrape3
  (:use [net.cgrand.enlive-html :as html]))

(def *base-url* "http://news.ycombinator.com/")

(defn fetch-url [url]
  (html-resource (java.net.URL. url)))

(defn only-domains []
  (map html/text
       (html/select (fetch-url *base-url*) [:td.title (but :a)])))

(defn print-domains []
  (doseq [line (only-domains)]
    (println (.trim line))))
