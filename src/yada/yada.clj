;; Copyright © 2015, JUXT LTD.

(ns yada.yada
  (:refer-clojure :exclude [partial])
  (:require
   [bidi.bidi :as bidi]
   yada.swagger
   yada.resources.atom-resource
   yada.resources.collection-resource
   yada.resources.file-resource
   yada.resources.string-resource
   yada.resources.url-resource
   yada.resources.sse
   yada.util
   [potemkin :refer (import-vars)]))

(import-vars
 [yada.handler handler yada]
 [yada.swagger swaggered]
 [yada.resource resource]
 [yada.protocols as-resource]
 [yada.util get-host-origin])

;; Convenience functions, allowing us to encapsulate the context
;; structure.
(defn content-type [ctx]
  (get-in ctx [:response :produces :media-type :name]))

(defn charset [ctx]
  (get-in ctx [:response :produces :charset :alias]))

(defn language [ctx]
  (get-in ctx [:response :produces :language]))

(extend-protocol bidi/Matched
  clojure.lang.APersistentMap
  (resolve-handler [this m]
    (when (= (:remainder m) "")
      (merge (dissoc m :remainder) {:handler (handler (resource this))})))
  (unresolve-handler [this m]
    (when (= this (:handler m)) "")))
