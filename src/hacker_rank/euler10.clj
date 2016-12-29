(ns hacker-rank.euler10)

;; The sum of the primes below 10 is 2+3+5+7 = 17
;; Find the sum of all primes not greater than given N

(defn lowest-divisor
  "Calculates the lowest divisor of n bigger than 1"
  [n]
  (loop [d 2 i 1]
    (cond
      (zero? (rem n d))         d  ;; d is lowest divisor
      (> (Math/pow d 2) n)      n  ;; n is a prime, so n
      :else (recur (+ d i) 2))))   ;; check next odd number


(defn prime? 
  "Returns true if n is a prime number, false otherwise"
  [n]
  (if (> n 1)
    (= (lowest-divisor n) n)
    false))


;; the lazy sequence of prime numbers
(def primes
  "An infinite and lazy sequence of prime numbers"
  (concat [2 3] (filter prime? (iterate #(+ 2 %) 5))))


(defn get-prime-sum
  "Finds the sum of all primes not greater than any given input[n]"
  [input]
  ; make sure input is sorted and of unique numbers
  (let [numbers (vec (apply sorted-set input))]
    ; iterate while there's numbers to process
    (loop [sieve primes numbers numbers sum 0 results {}]
      (if-not (empty? numbers)
      
        (let [[x & xs] numbers
              [p & ps] sieve]
        
          (if (<= p x)
            (recur ps numbers (+ sum p) results)
            (recur sieve xs sum (assoc results x sum))))
            
        ; returns a vector with the sums for every input number
        (into [] (map #(get results %) input))))))

(comment
  ;; Reads HackerRank input data
  (defn get-input []
    (let [total-lines (Integer/parseInt (read-line))]
      (for [line (range 0 total-lines)]
        (Integer/parseInt (read-line)))))

  ;; Solves the algorithm
  (let [input (get-input)
        result (get-prime-sum input)]
      (dotimes [n (count result)]
        (println (result n))))
)




