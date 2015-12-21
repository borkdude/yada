;; Copyright © 2015, JUXT LTD.

(ns yada.dev.config
  (:require
   [schema.core :as s]
   [aero.core :refer (read-config)]
   [phonebook.schema :refer [PhonebookEntry Phonebook UserPort]]
   ))

(s/defschema ConfigSchema
  {:scheme (s/enum "http" "https")
   :host String
   :ports {:docsite UserPort
           :console UserPort
           :cors-demo UserPort
           :talks UserPort
           }
   :phonebook {:port UserPort
               :entries Phonebook}
   :selfie {:port UserPort
            }})

(defn config
  "Return a map of the static configuration used in the component
  constructors."
  [profile]
  (read-config
   "dev/config.edn"
   {:profile profile
    :schema ConfigSchema}))

(defn docsite-port [config]
  (-> config :ports :docsite))

(defn console-port [config]
  (-> config :ports :console))

(defn cors-demo-port [config]
  (-> config :ports :cors-demo))

(defn talks-port [config]
  (-> config :ports :talks))

(defn phonebook-port [config]
  (-> config :phonebook :port))

(defn selfie-port [config]
  (-> config :selfie :port))

(defn docsite-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (docsite-port config)]
         (str ":" port))))

(defn cors-demo-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (cors-demo-port config)]
         (str ":" port))))

(defn talks-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (talks-port config)]
         (str ":" port))))

(defn phonebook-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (phonebook-port config)]
         (str ":" port))))

(defn selfie-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (selfie-port config)]
         (str ":" port))))

(defn console-origin [config]
  (str (:scheme config)
       "://"
       (:host config)
       (when-let [port (console-port config)]
         (str ":" port))))
