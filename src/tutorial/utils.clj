(ns tutorial.utils
  (:use [net.cgrand.enlive-html :as html]))

(defn render [t]
  (apply str t))

(defmulti parse-int type)
(defmethod parse-int java.lang.Integer [n] n)
(defmethod parse-int java.lang.String [s] (Integer/parseInt s))

(defmacro block [sym]
  `(fn [n#] (if (nil? ~sym) n# ((html/substitute ~sym) n#))))

(defmacro maybe-content [expr & rest]
  `(fn [n#] (if (nil? ~expr)
              (if-let [default# ~(first rest)]
                ((html/content default#) n#)
                n#)
              ((html/content ~expr) n#))))
