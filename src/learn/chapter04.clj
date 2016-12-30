(ns learn.chapter04
  (:require [clojure.algo.generic.math-functions :as math :refer :all]))

(defn ad-hoc-type-name [thing]
  (condp = (type thing)
    java.lang.String                "String"
    clojure.lang.PersistentVector   "Vector"))

(def type-name-implementations
  {java.lang.String               (fn [thing] "String")
   clojure.lang.PersistentVector  (fn [thing] "Vector")})

(defn open-ad-hoc-type-name [thing]
  (let [dispatch-value (type thing)]
    (if-let [implementation (get type-name-implementations dispatch-value)]
      (implementation thing)
      (throw (IllegalArgumentException. (str "No implementation found for " dispatch-value))))))

(comment
  ;;just testing imports and dependencies
  (defn my-pow [x y]
    (math/pow x y)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; POLYMORPHISM AND MULTIMETHODS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def example-user {:login "rob" :referrer "mint.com" :salary 100000})


(defn fee-amount 
  "Calculates the fee you pay to affiliates based on their annual salary"
  [percentage user]
  (with-precision 16 :rounding HALF_EVEN
    (* 0.01M percentage (:salary user))))

(comment 
  
  (defn affiliate-fee 
          "Calculates the fee for an affiliate user based on their referrer and annual salary"
          [user]
          ;;this is an example of close dispatch
          ;;where the referrer is the dispatch criteria
          ;;the problem is that adding referrers requires code changes
          (case (:referrer user)
            "google.com"  (fee-amount 0.01M user)
            "mint.com"    (fee-amount 0.03M user)
            (fee-amount 0.02M user))))
  


;;MULTIMETHODS TO THE RESCUE!

(defmulti affiliate-fee 
  (fn [user] (:referrer user)))

(defmethod affiliate-fee "mint.com"
  [user] (fee-amount 0.03M user))

(defmethod affiliate-fee "google.com"
  [user] (fee-amount 0.01M user))

(defmethod affiliate-fee :default
  [user] (fee-amount 0.02M user))

