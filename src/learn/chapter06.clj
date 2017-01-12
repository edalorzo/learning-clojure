(ns learn.chapter06)

;;;;;;;;;;;;;;;;;;;;;;;
;; USING REFS
;;;;;;;;;;;;;;;;;;;;;;;

(def all-users (ref {}))

(comment 
  
  ;; You can dereference the ref in two ways
  (deref all-users)
  
  ;;using a reader macro
  @all-users
)

;;MUTATING REFERENCES: ref-set, alter or commute

;; Since refs are meant for situations where multiple threads
;; need to coordiante changes, the Clojure runtime demands that
;; mutating a ref be done inside a STM transaction.

(dosync (ref-set all-users {}))

;; Typically, a ref is mutated by taking its current value,
;; applying a function to it, and storing the new value back into it.

(defn new-user [id login monthly-budget]
  {:id id :login login :montly-budge monthly-budget :total-expenses 0})

(defn add-new-user-1 [login budget-amount]
  (dosync
    (let [current-number (count @all-users)
          user (new-user (inc current-number) login budget-amount)]
      (alter all-users assoc login user))))

(defn add-new-user-2 [login budget-amount]
  (dosync
    (let [current-number (count @all-users)
          user (new-user (inc current-number) login budget-amount)]
      (commute all-users assoc login user))))


;;;;;;;;;;;;;;;;;;;;;;;
;; AGENTS
;;;;;;;;;;;;;;;;;;;;;;;

;; creating the agent
(def total-cpu-time (agent 0))

(comment 
  ;; we can use the same mechanisms to dereference an agent
  (deref total-cpu-time)
  @total-cpu-time
)

;; mutating agents with send and send-off

;; Changes made to the agent will happen asynchronously.
;; These changes are made by sending an action function to the agent.
;; which runs it on a separate thread at a later time.

(defn increase-cpu-time-1 [time]
  (send total-cpu-time + time))


;; send will use an event loop to make sure only one action
;; at the time runs on the agent, therefore we cannot do
;; blocking operations on the agent, for that matter we have send-off

(defn sum-it [a b]
  (Thread/sleep 10000)
  (+ a b))

(defn increase-cpu-time-2 [time]
  (send-off total-cpu-time sum-it time))

(comment
  ;; if we need to wait for a given action to complete
  ;; we can use await for that matter, as  way to turn
  ;; asynchronous code into a synchronous code again.

  (await total-cpu-time) ;; this will block until done!
  (await-for 1000 total-cpu-time) ;;waits at most 1000 ms.
  
)

(def bad-agent (agent 100))

(comment 
  (send bad-agent / 0) ;;sends a message that will surely fail
  (println @bad-agent) ;; yields the last consistent value 100
  
  ;; if we try to send any further message it will fail with its
  ;; latest exception thrown
  (send bad-agent / 2) ;;yields ArithmeticException: Divide by zero
  (agent-error bad-agent) ;; gets the exception thrown
  
  ;; to make the agent usable again, we must clear the errors
  (clear-agent-errors bad-agent)
  (send bad-agent / 2)  ;;now this succeeds
)

;;;;;;;;;;;;;;;;;;
;; ATOMS
;;;;;;;;;;;;;;;;;;

(def total-rows (atom 0))

(comment 
  
  ;; You can dereference the ref in two ways
  (deref total-rows)
  
  ;;using a reader macro
  @total-rows)


(reset! total-rows 10)
(swap! total-rows + 100)

;;;;;;;;;;;;;;;;;;
;; VARS
;;;;;;;;;;;;;;;;;;
(comment
  (def ^:dynamic *mysql-host*)

  (defn db-query [db]
    (binding [*mysql-host*]
      (count *mysql-host*)))

  (def mysql-hosts ["test-mysql" "dev-mysql" "stagin-mysql"])
  (pmap db-query mysql-hosts))
  
;;;;;;;;;;;;;;;;;;
;; WATCHERS
;;;;;;;;;;;;;;;;;;

(comment
  (defn on-change [the-key the-ref old-val new-val]
    (println "Just saw change from " old-val " to " new-val))

  (add-watch total-rows :total-watcher on-change)
  (swap! total-rows inc)
  (remove-watch total-rows :total-watcher))

;;;;;;;;;;;;;;;;;;
;; FUTURES
;;;;;;;;;;;;;;;;;;

(defn long-calculation [a b]
  (Thread/sleep 5000)
  (* a b))

(defn long-run []
  (let [x (long-calculation 11 13)
        y (long-calculation 13 17)
        z (long-calculation 17 19)]
    (* x y z)))

(defn fust-run []
  (let [x (future (long-calculation 11 13))
        y (future (long-calculation 13 17))
        z (future (long-calculation 17 19))]
    (* @x @y @z)))

(defn with-promise []
  (let [p (promise)]
    (future (Thread/sleep 5000)
      (deliver p :done))
    @p))



