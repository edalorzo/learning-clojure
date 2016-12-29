(ns hacker-rank.fibonacci)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; FIBONACCI NUMBERS
;; https://www.hackerrank.com/challenges/functional-programming-warmups-in-recursion---fibonacci-numbers
;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment 

;; Non-tail recursive fibonacci sequence
  
(defn fibonacci [n]
  {:pre [(> n 0)]}
  (cond (= n 1) 0
        (= n 2) 1
        :otherwise (+ (fibonacci (- n 1))  (fibonacci (- n 2)))))

)

(comment
  
;; Tail recursive fibonacci sequence

(defn fibonacci [n]
  {:pre [(> n 0)]}
  (if (= n 1) 0
    (loop [fibs [0 1]  i 2]
            (if-not (= n i)
              (let [[a b] fibs c a a b b (+ b c)]
                (recur [a b] (inc i)))
              (last fibs)))))
)


;; Fibonacci O(1)

(defn fibonacci [n]
  {:pre [(> n 0)]}
  (if (zero? n) 1
    (let [n (dec n)
          sqrt5 (Math/sqrt 5.0)
          fi (/ (+ 1.0 sqrt5) 2.0)
          psi (/ (- 1.0 sqrt5) 2.0)
          fib (/ (- (Math/pow fi n) (Math/pow psi n)) sqrt5)]
      (int fib))))


(comment
  
;; Reads HackerRank input data and solves the problem
  
(let [n (Integer/parseInt (read-line))]
  (println (fibonacci n)))
  
)
  