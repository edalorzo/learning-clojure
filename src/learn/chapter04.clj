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

(comment 
  ;;multimethod defines dispatch criteria

  (defmulti affiliate-fee 
    (fn [user] (:referrer user))) ;;this is equivalent to just using :referrer

  ;; implementation of the multimethod

  (defmethod affiliate-fee "mint.com"
    [user] (fee-amount 0.03M user))

  (defmethod affiliate-fee "google.com"
    [user] (fee-amount 0.01M user))

  (defmethod affiliate-fee :default
    [user] (fee-amount 0.02M user))
  
)

;;the default method can also be defined in the dispatch

(defmulti affiliate-fee :referrer :default "*")

(defmethod affiliate-fee "mint.com"
  [user] (fee-amount 0.03M user))

(defmethod affiliate-fee "google.com"
  [user] (fee-amount 0.01M user))

(defmethod affiliate-fee "*"
  [user] (fee-amount 0.02M user))

;;MULTIPLE DISPATCH

(def user-1 {:login "rob" :referrer "mint.com" :salary 100000 :rating :rating/bronze})
(def user-2 {:login "gordon" :referrer "mint.com" :salary 80000 :rating :rating/silver})
(def user-3 {:login "kyle" :referrer "google.com" :salary 90000 :rating :rating/gold})
(def user-4 {:login "celeste" :referrer "yahoo.com" :salary 70000 :rating :rating/platinum})

;-----------------------------------------------------
; Affiliate     Profit Rating       Fee (% of salary)
;-----------------------------------------------------
; mint.com      Bronze              0.03
; mint.com      Silver              0.04
; mint.com      Gold/Platinum       0.05
; google.com    Gold/Platinum       0.03
;-----------------------------------------------------

(comment
  
;; the fee category will be our new dispatch function
(defn fee-category [user]
  [(:referrer user) (:rating user)])

(defmulti profit-based-affiliate-fee fee-category)

(defmethod profit-based-affiliate-fee ["mint.com" :rating/bronze]
  [user] (fee-amount 0.03M user))

(defmethod profit-based-affiliate-fee ["mint.com" :rating/silver]
  [user] (fee-amount 0.04M user))

;; notice that these two methods are the same thing
;; this looks like ad hoc polymorphism
;; a hint that these two are the same
;; perhaps this a job for subtype polymorphism
(defmethod profit-based-affiliate-fee ["mint.com" :rating/gold]
  [user] (fee-amount 0.05M user))

(defmethod profit-based-affiliate-fee ["mint.com" :rating/platinum]
  [user] (fee-amount 0.05M user))

;; notice that these two other methods are the same thing
;; this looks like ad hoc polymorphism
;; a hint that these two are the same
;; perhaps this a job for subtype polymorphism
(defmethod profit-based-affiliate-fee ["google.com" :rating/gold]
  [user] (fee-amount 0.03M user))

(defmethod profit-based-affiliate-fee ["google.com" :rating/platinum]
  [user] (fee-amount 0.03M user))

(defmethod profit-based-affiliate-fee :default
  [user] (fee-amount 0.02M user))

)

;;MULTIPLE DISPATCH AND SUBTYPE POLYMORPHISM

;
;                      ┌────────┐                   
;                      │ Profit │
;                      │ Rating │ 
;                      └────────┘
;                          ^
;                          |
;             +------------+------------+
;             |                         |
;         ┌────────┐               ┌────────┐ 
;         │ Basic  │               │Premier │ 
;         └────────┘               └────────┘ 
;             ^                        ^
;             |                        |
;      +------+-----+            +-----+------+
;      |            |            |            |
;  ┌────────┐   ┌────────┐   ┌────────┐  ┌────────┐
;  │ Bronze │   │ Silver │   │  Gold  │  │Platinum│
;  └────────┘   └────────┘   └────────┘  └────────┘

;; USING DERIVE TO CREATE THE HIERARCHY
;; derive x from y (i.e. x is a kind of y)

(derive :rating/bronze :rating/basic)
(derive :rating/silver :rating/basic)
(derive :rating/gold :rating/premier)
(derive :rating/platinum :rating/premier)
(derive :rating/basic :rating/ANY)
(derive :rating/premier :rating/ANY)

(isa? :rating/bronze :rating/basic) ;;yields true
(parents :rating/god) ;;yields :rating/premier
(ancestors :rating/bronze) ;; yields #{:rating/ANY :rating/basic}
(descendants :rating/basic) ;;yields #{:rating/bronze :rating/silver}

(defmulti greet-user :rating)

(defmethod greet-user :rating/basic
  [user] (str "Hello " (:login user) "."))

(defmethod greet-user :rating/premier
  [user] (str "Welcome, " (:login user) ", valued affiliate member!"))

(comment
  ;;if now we do this, we can see the the multimethod criteria
  ;;takes into accout the hierarchical aspect to determine dispatch method
  (map c4/greet-user [c4/user-1 c4/user-2 c4/user-3 c4/user-4])
)


;; the fee category will be our new dispatch function
(defn fee-category [user]
  [(:referrer user) (:rating user)])

(defmulti profit-based-affiliate-fee fee-category)

(defmethod profit-based-affiliate-fee ["mint.com" :rating/bronze]
  [user] (fee-amount 0.03M user))

(defmethod profit-based-affiliate-fee ["mint.com" :rating/silver]
  [user] (fee-amount 0.04M user))

(defmethod profit-based-affiliate-fee ["mint.com" :rating/premier]
  [user] (fee-amount 0.05M user))


(defmethod profit-based-affiliate-fee ["google.com" :rating/premier]
  [user] (fee-amount 0.03M user))

(comment 
  ;;this default would not work now
  (defmethod profit-based-affiliate-fee :default
    [user] (fee-amount 0.02M user))
)


(defmulti size-up (fn [observer observed]
                    [(:rating observer) (:rating observed)]))

(defmethod size-up [:rating/platinum :rating/ANY]
  [_ observed] (str (:login observed) " seems scrawny."))

(defmethod size-up [:rating/ANY :rating/platinum]
  [_ observed] (str (:login observed) " shimmers with unearthly light."))

(comment
  
  ;;the following invocation would cause ambiguties unless we use a prefer method
  (size-up {:rating :rating/platinum} user-4)

  ;;prefer the first dispatch over the second one  
  (prefer-method size-up [:rating/ANY :rating/platinum] 
               [:rating/platinum :rating/ANY])
)


(comment

  ;; since the clojure generic math function uses multimethods for its definitions
  ;; I can take their pow function that works on doubles and make an implementation 
  ;; that works on longs, and voila, it now works pefectly
  
  (defmethod math/pow [java.lang.Long java.lang.Long]
      [x y] (long (Math/pow x y)))  
  
  ;;just testing imports and dependencies
  (defn my-pow [x y]
    (math/pow x y))
  
)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; USER-DEFINED HIERARCHIES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def my-hierarchy (make-hierarchy))
(derive my-hierarchy :a :letter) ;; Meaning :a is a :letter
(def my-hierarchy 
  (-> my-hierarchy
      (derive :a :letter)
      (derive :b :letter)
      (derive :c :letter)))

(isa? my-hierarchy :a :letter) ;;yields true
(isa? my-hierarchy :d :letter) ;;yields false
(parents my-hierarchy :a) ;; yields #{:letter}
(descendants my-hierarchy :letter) ;;yields #{:a :b :c}

(defmulti letter? identity :hierarchy #'my-hierarchy)
(defmethod letter? :letter
  [_] true)
  




