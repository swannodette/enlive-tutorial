(ns macroology
  (:require [net.cgrand.enlive-html :as html])
  (:use [clojure.contrib.duck-streams :only [slurp*]])
  (:use [clojure.contrib.seq-utils :only [flatten]])
  (:import [java.io ByteArrayInputStream]))

(defn to-in-s [str] (ByteArrayInputStream. (.getBytes str "UTF-8")))

(def *nodes-with-id*
     (html/selector #{[[:* (html/attr? :id)]]}))

(defn sel-for-node [{tag :tag, attrs :attrs}]
  (let [css-id (:id attrs)]
    `([~(keyword (str (name tag) "#" css-id))] (html/content ~(symbol css-id)))))

(defmacro templ [name rsrc]
  (let [nodes (select (html-resource (eval rsrc)) *class-or-id-selector*)]
    `(deftemplate ~name (html-resource ~rsrc)
       [~'ctxt]
       ~@(reduce concat (map sel-for-node nodes)))))

(defmacro templ-str [rsrc])

(comment
  (deftemplate foo (to-in-stream "<span id='foo'></span>")
    [ctxt]
    [:#foo] (content (:foo ctxt)))

  (templ foo *rsrc*)

  (def *markup* "<span id='foo'></span><div class='bar'><p id='bar'></p></div>")
  (def *rsrc* (to-in-s *markup*))
  (def *foo* (select (html-resource *rsrc*) *nodes-with-id*))
  (reduce concat (map sel-for-node *foo*))

  ; works
  (map sel-for-node *foo*)
  )