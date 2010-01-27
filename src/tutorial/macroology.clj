(ns macroology
  (:require [net.cgrand.enlive-html :as html])
  (:import [java.io ByteArrayInputStream]))

(defn to-in-s [str] (ByteArrayInputStream. (.getBytes str "UTF-8")))

(def *class-or-id-selector*
     (html/selector #{[[:* (html/attr? :id)]], [[:* (html/attr? :class)]]}))

(defn sel-for-node [{tag :tag, attrs :attrs}]
  (let [css-id (:id attrs)]
    `([~(keyword (str tag "#" css-id))] (html/content ~(symbol css-id)))))

(defmacro templ [name rsrc]
  (do
    (println "WTF")
   (let [nodes (select (html-resource (eval rsrc)) *class-or-id-selector*)]
     (print nodes)
     `(deftemplate ~name ~rsrc
        [~'ctxt]
        ~@(map sel-for-node nodes)))))

(defmacro templ-str [rsrc])

(comment
  (deftemplate foo (to-in-stream "<span id='foo'></span>")
    [ctxt]
    [:#foo] (content (:foo ctxt)))

  (templ foo *rsrcr*)

  (def *markup* "<span id='foo'></span><div class='bar'></div>")
  (def *rsrc* (to-in-s *markup*))
  (def *foo* (select (html-resource *rsrc*) *class-or-id-selector*))
  )