(ns learn.chapter03)

;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; METADATA
;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment

;; there's a better way with the reader macro: ^{}
 (def untrusted
   (with-meta {:command "delete-table" :subject "users"}
              {:safe false :io true})))


(def untrusted ^{:safe false :io true :retry 1}
  {:command "delete-table" :subject "users"})

(comment
  ;; to see the contents of the metadata you do
  (meta untrusted)) ;;{:safe false :io true :retry 1}


;; when new values are created out of old ones
;; the metadata is still kept in the new structure
;; do a (meta still-untrusted) to see it
(def still-untrusted (assoc untrusted :complete? false))

;; functions can also have meta data
(defn ^{:safe true :console true :doc "Testing meta in functions"}
  testing-meta []
  (println "Hello from meta!"))

(comment
  ;; in order to see the metadata of a function you need to
  ;; look into its var, since metadata is stored in the var
  ;; and not in the function itself
  (meta (var testing-meta))

  ;; alternatively
  (meta #'testing-meta))


;; metadata can be used with type parameters to provide
;; JAVA TYPE HITS!!!!
(defn string-length [^String s]
  (.length s))

(comment
  ;; if you want the compiler to let you know of the importance
  ;; of these type annotations turn on reflection warnings in code
  (set! *warn-on-reflection* true))


(comment
  ;; you can see the metadata of the arguments as well
  ;; {:arglist ([s]) ...}
  (-> (meta #'string-length)
      (:arglists)
      (first)
      (first)
      (meta)) ;;yields {:tag String}

  ;;alternatively
  (meta (first (first (:arglist (meta #'string-length))))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ARRAYS OF SPECIFIC TYPES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn array-type [kclass]
  (.getName (class (make-array kclass 0))))

(comment

  (array-type BigDecimal))

  ;; yields [Ljava.math.BigDecimal]
  ;; which is the JVM notation for an object array of tye BigDecimal


;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; EXCEPTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn safe-average [numbers]
  (let [total (apply + numbers)]
    (try
      (/ total (count numbers))
      (catch ArithmeticException e
        (println "Divided by zero!")
        0))))


(defn complex-average [numbers]
  (let [total (apply + numbers)]
    (try
      (println "Attempting division...")
      (/ total (count numbers))
      (catch ArithmeticException e "DIVIDE BY ZERO!")
      (catch RuntimeException e "Runtime exception!")
      (catch Throwable e "Unknown exception encountered!")
      (finally (println "done.")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; FUNCTIONS
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Functions may have multiple arities
(defn total-cost

   "Determines cost of goods of a number of items of a given price"

  ([item-cost number-of-items]
   (* item-cost number-of-items))

  ([item-cost]
   (total-cost item-cost 1)))

;; You can define precondition and postconditions
(defn item-total [price quantity discount-percentage]
  {:pre [(> price 0) (> quantity 0)]
   :post [(> % 0)]}
  (->> (/ discount-percentage 100)
       (- 1)
       (* price quantity)
       float))

(comment
  (item-total 100 2 0) ;;yields 200.0
  (item-total 100 2 10) ;;yields 180.0
  (item-total 100 -2 10) ;;AssertionError for precondition
  (item-total 100 2 110)) ;;Assertion for postcondition (BUG!!!)



;; Variadic functions
(defn total-all-numbers [& numbers]
  (apply + numbers))

(comment
  ;; now we can invoke the function as
  (total-all-numbers)
  (total-all-numbers 1)
  (total-all-numbers 1 2)
  (total-all-numbers 1 2 3)
  (total-all-numbers 1 2 3 4)
  (total-all-numbers 1 2 3 4 5)) ;...


(defn many-arities
  ([]             0)
  ([a]            1)
  ([a b c]        3)
  ([a b c & more] "variadic"))

;; CLASSICAL RECURSION
(defn count-down [n]
  (when-not (zero? n)
    (when (zero? (rem n 100))
      (println "count-down:" n))
    (count-down (dec n))))

(defn quicksort [xs]
  (if (not-empty xs)
    (let [x (first xs)
          xs (rest xs)
          ls (quicksort (filter #(< % x) xs))
          rs (quicksort (filter #(> % x) xs))]
      (concat ls [x] rs))))


;; TAIL RECURSION
(defn count-downr [n]
  (when-not (zero? n)
    (if (zero? (rem n 100))
      (println "count-down:" n))
    (recur (dec n))))

;; MUTUALLY RECURSIVE
(comment

  (declare is-odd?)

  (defn is-even? [n]
    (if (zero? n)
      true
      (is-odd? (dec n))))

  (defn is-odd? [n]
    (if (zero? n)
      false
      (is-even? (dec n)))))


;; TAIL MUTUALLY RECURSIVE
(comment
  (declare my-is-odd?)

  (defn- my-is-even? [n]
    (if (zero? n)
      true
      (fn [](my-is-odd? (dec n)))))

  (defn- my-is-odd? [n]
    (if (zero? n)
      false
      (fn [] (my-is-even? (dec n)))))

  (comment
    (trampoline is-odd? 5)
    (trampoline is-even? 4))


  (def is-even? (partial trampoline my-is-even?))
  (def is-odd? (partial trampoline my-is-odd?)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; SCOPE RULES
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment
;; “Vars in Clojure are, in some ways, similar to globals in other languages.
;; Vars are defined at the top level of any namespace, using the def special form.

 (def MAX-CONNECTIONS 10)  ;;bound var
 (def ^:dynamic RABBITMQ-CONNECTION) ;;unbound var

 (binding [RABBITMQ-CONNECTION (new-connection)]
   ())
   ;; do something with rabbit mq connection here


 (defn *db-host* "localhost")

 (defn expense-report [start-date end-date]
   (println *db-host*)) ;;do real work here

;; by changing your bindings you can test you code
;; locally or just as easily move it to production

 (binding [*db-host* "production"]
   (expense-report "2010-01-01" "2010-01-07")))



;; DYNAMIC BINDING

(comment

  ;; through manual bindings you control
  ;; the values of your vars at runtime

  (def ^:dynamic *eval-me* 10)
  (defn print-the-var [label]
    (println label *eval-me*))

  (defn my-binding-demo []
    (print-the-var "A:")
    (binding [*eval-me* 20]
      (print-the-var "B:")
      (binding [*eval-me* 30]
        (print-the-var "C:"))
      (print-the-var "D:"))
    (print-the-var "E:")))



;; USING DYNAMIC BINDING FOR AOP PROGRAMMING STYLE

(comment

  (defn ^:dynamic twice [x]
    (println "Original Function")
    (* x x))

  (defn call-twice [y]
    (twice y))

  (defn with-log [f log-entry]
    (fn [& args]
      (println log-entry)
      (apply f args)))

  (binding [twice (with-log twice "Calling the twice function")]
    (call-twice 20)))



(comment
  ;; a classical mistake with dynamic binding

  (def ^:dynamic *factor* 10)
  (defn multiply [x]
    (* x *factor*))

  (map multiply [1 2 3 4 5]) ;;in console yields [10 20 30 40 50]

  (binding [*factor* 20]
    (map multiply [1 2 3 4 5]))

  ;; still yields [10 20 30 40 50] in console
  ;; because printing happens after binding is done
  ;; and map is a lazy sequence

  ;;we can either do

  (binding [*factor* 20]
    (apply println (map multiply [1 2 3 4 5])))

  (binding [*factor* 20]
    (doall (map multiply [1 2 3 4 5]))))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; DESTRUCTURING
;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment

  ;; This function can be better written
  (defn describe-salary [person]
    (let [first (:first-name person)
          last (:last-name person)
          annual (:salary person)]
      (println first last "earns" annual)))

  ;(require '[learn.chapter03 :as chap :refer [describe-salary]] :reload)

  (defn describe-salary [{first :first-name
                          last :last-name
                          annual :salary}]
    (println first last "earns" annual))

    ;;provide optional paramters
  (defn describe-salary [{first :first-name
                            last :last-name
                            annual :salary
                            bonus :bonus-percentage
                            :or {bonus 5}}]
      (println first last "earns" annual "with a bonus of" bonus))

  ;;gain access to the entire map
  (defn describe-salary [{first :first-name
                            last :last-name
                            annual :salary
                            bonus :bonus-percentage
                            :or {bonus 5}
                            :as p}]
      (println first last "earns" annual "with a bonus of" bonus)
      (println "And also the full map is" p))


  (describe-salary {:first-name "Edwin" :last-name "Dalorzo" :salary 100})
  (describe-salary {:first-name "Karla" :last-name "Fallas" :salary 100 :bonus-percentage 50})

  ;;simplify key usage
  (defn greet-user [{:keys [first-name last-name]}]
    (println "Welcome," first-name last-name))

  (greet-user {:first-name "Edwin" :last-name "Dalorzo"}))





(comment

  ;;with vectors

  (defn print-amounts [[amount-1 amount-2]]
    (println "Amounts are"  amount-1 amount-2))

  (print-amounts [30.39 31.45])

  (defn print-amount-multiple [[amount-1 amount-2 & remaining]]
    (println "Amounts are" amount-1 amount-2 "and" remaining))

  (print-amount-multiple [10.95 31.45 22.36 2.95])

  (defn print-amount-multiple [[amount-1 amount-2 & remaining :as all]]
    (println "Amounts are" amount-1 amount-2 "and" remaining)
    (println "Also, all the amounts are" all))

  (print-amount-multiple [10.95 31.45 22.36 2.95])


  ;;nested vectors
  (defn print-first-category [[[category amount] & _]]
    (println "First category was:" category)
    (println "First amount was:" amount))

  (print-first-category [[:books 49.95] [:coffee 4.95] [:caltrain 2.25]]))

(defn greeting
  [name]
  (str "Hello, " name "!"))
