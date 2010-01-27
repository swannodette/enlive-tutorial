(ns tutorial.macroology
  (:use [tutorial.utils :only [render]])
  (:require [net.cgrand.enlive-html :as html])
  (:import [java.io ByteArrayInputStream]))

(defn to-in-s [str] (ByteArrayInputStream. (.getBytes str "UTF-8")))

(def *nodes-with-id*
     (html/selector #{[[:* (html/attr? :id)]]}))

(defn sel-for-node [{tag :tag, attrs :attrs}]
  (let [css-id (:id attrs)]
    `([~(keyword (str (name tag) "#" css-id))] (html/content (~(keyword css-id) ~'ctxt)))))

(defmacro templ [name rsrc]
  (let [nodes (html/select (html/html-resource (eval rsrc)) *nodes-with-id*)]
    `(html/deftemplate ~name ~rsrc
       [~'ctxt]
       ~@(reduce concat (map sel-for-node nodes)))))

(defmacro templ-str [name rsrc-str]
  (let [nodes (html/select (html/html-resource (to-in-s (eval rsrc-str))) *nodes-with-id*)]
    `(html/deftemplate ~name (to-in-s ~rsrc-str)
       [~'ctxt]
       ~@(reduce concat (map sel-for-node nodes)))))

(comment
  (def *markup* "<span id='foo'></span><div class='bar'><p id='bar'></p></div>")
  (templ-str foo *markup*)
  (render (foo {:foo "cool" :bar "awesome"}))

  (templ bar "tutorial/introspect.html")
  (render (bar {:foo "cool" :bar "awesome"}))
  )