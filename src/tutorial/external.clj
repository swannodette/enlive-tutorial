(ns tutorial.external
  (:use [clojure.contrib.java-utils :only [file]])
  (:require [tutorial.macroology :as t]))

(def *webdir* "/Users/davidnolen/development/clojure/enlive-tutorial/src/tutorial/")

(t/templ-str foo "<span id='foo'></span><div id='bar'></div")
(apply str (foo {:foo "Hello", :bar "world!"}))

(t/templ foo (file *webdir* "introspect.html"))
(apply str (foo {:foo "Hello", :bar "world!"}))

;; can't eval locals
(let [markup "<span id='baz'></span>"]
  (apply str (t/templ baz markup)))