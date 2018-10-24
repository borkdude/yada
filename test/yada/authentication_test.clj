;; Copyright © 2014-2017, JUXT LTD.

(ns yada.authentication-test
  (:require
   [clojure.test :refer :all :exclude [deftest]]
   [schema.core :as s]
   [schema.test :refer [deftest]]
   [yada.schema :as ys]
   [yada.security :refer [authenticate verify]]
   [yada.test-util :refer [wrap-speculative]]))

(use-fixtures :once wrap-speculative)

;; We create some fictitious schemes, just for testing

(defmethod verify "S1"
  [ctx {:keys [authenticated]}]
  authenticated)

(defmethod verify "S2"
  [ctx {:keys [authenticated]}]
  authenticated)

(defn validate-ctx [ctx]
  (s/validate {:resource ys/Resource} ctx))

(deftest authenticate_test
  (testing "Across multiple realms and schemes"
    (is (=
         ["S1 realm=\"R1\", S2 realm=\"R1\""
          "S1 realm=\"R2\", S2 realm=\"R2\""]
         (-> {:resource
              {:methods {}
               :access-control
               {:realms
                {"R1" {:authentication-schemes
                       [{:scheme "S1"
                         :verify (constantly false)}
                        {:scheme "S2"
                         :verify (constantly false)}
                        ]}
                 "R2" {:authentication-schemes
                       [{:scheme "S1"
                         :verify (constantly false)}
                        {:scheme "S2"
                         :verify (constantly false)}]}}}}}
             validate-ctx
             authenticate
             (get-in [:response :headers "www-authenticate"])))))

  (testing "Across multiple realms and schemes, with some prior authentication in one of the realms"
    (let [ctx {:resource
               {:access-control
                {:realms
                 {"R1" {:authentication-schemes
                        [{:scheme "S1"
                          :authenticated false}
                         {:scheme "S2"
                          :authenticated {:user "george"
                                          :roles #{:pig}}}
                         ]}
                  "R2" {:authentication-schemes
                        [{:scheme "S1"
                          :authenticated false}
                         {:scheme "S2"
                          :authenticated false}]}}}}}
          result (authenticate ctx)]

      ;; We have successfully verified in realm R1
      (is (= {"R1" {:user "george"
                    :roles #{:pig}}}
             (:authentication result)))

      ;; But not in realm R2, so we tell the user-agent how to do so
      (is (= ["S1 realm=\"R2\", S2 realm=\"R2\""]
             (get-in result [:response :headers "www-authenticate"]))))))

;; TODO: Authorization test

;; TODO: Investigate roles inheritance

;; Roles: Use [:and ...] for conjunctions, [:or ...] for disjunctions

;; Each realm can be a hierarchy

;; (derive ::a ::b)
;; (derive ::b ::c)
;; (isa? ::a ::c)
