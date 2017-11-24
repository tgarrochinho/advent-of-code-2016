(ns bathsecurity.core
  (:require [clojure.string :as str])
  (:gen-class))

; ###################################
; keypad button coordinates and value
; ###################################

(defn- previous-lines-cumulative-count [keypad offset]
  "Cumulative element count for previous offset lines"
  (->> (map #(count %) keypad)
       (reduce #(conj %1 (+ (reduce + 0 %1) %2)) [])
       (take-while #(< %1 offset))))

(defn- line-previous-count [keypad offset]
  "Previous offset line element count"
  (let [p-lines-counter (last (previous-lines-cumulative-count keypad offset))]
    (if (nil? p-lines-counter) 0 p-lines-counter)))

(defn- keypad-line [keypad offset]
  "Get offset keypad line number"
  (count (previous-lines-cumulative-count keypad offset)))

(defn- keypad-col [keypad offset]
  "Get offset keypad column number"
  (let [line (keypad-line keypad offset)
        p-count (line-previous-count keypad offset)]
    (->> (map vector (iterate inc 0) (nth keypad line))
         (map #(first %))
         (take-while #(< (+ % p-count) offset))
         (last))))

(defn- keypad-button-coordinates [keypad offset]
  "Convert 1d offset into 2d keypad coordinates: [line col]"
  [(keypad-line keypad offset)
   (keypad-col keypad offset)])

(defn- keypad-button-value [keypad coordinates]
  "Get keypad value with 2d coordinates"
  (get-in keypad coordinates))

; ###################################
; keypad solver
; ###################################

(defn- move [keypad coordinates instruction]
  "Get next move coordinates"
  (let [change
        (case instruction
          \U [-1 0]
          \D [1 0]
          \L [0 -1]
          \R [0 1]
          :else [0 0])
        next (into [] (map + coordinates change))]
    (if (keypad-button-value keypad next) next coordinates)))

(defn- solve-line [keypad coordinates line-instructions]
  "Solve single line"
  (reduce #(move keypad %1 %2) coordinates (seq line-instructions)))

(defn- solve-lines [keypad start-button-coordinates instructions]
  "Solve multiples lines tracking all movements and values"
  (reduce
    #(conj %1
           (let [solved-coordinates (solve-line keypad (first (last %1)) %2)]
                [solved-coordinates (keypad-button-value keypad solved-coordinates)]))
    [[start-button-coordinates (keypad-button-value keypad start-button-coordinates)]]
    instructions))

(defn solve [keypad offset instructions]
  "Main solver"
  (->> (solve-lines keypad (keypad-button-coordinates keypad offset) instructions)
       (rest)
       (map #(last %))
       (apply str)))
