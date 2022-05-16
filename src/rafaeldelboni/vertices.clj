(ns rafaeldelboni.vertices
  "Based on https://stackoverflow.com/a/25276331"
  (:import [java.util Random]))

(set! *warn-on-reflection* true)

(defn rand-seeded
  "Given an seed return a random number between min and max args"
  ^Double
  [^Long seed ^Double min ^Double max]
  (if (= min max)
    min
    (-> (Random. seed)
        (.doubles min max)
        .findFirst
        .getAsDouble)))

(defn rand-gauss-seeded
  "Given an seed returns a random floating point value based on the given mean
  and standard deviation for the Gaussian distribution."
  ^Double
  [^Long seed ^Double mu ^Double sigma]
  (-> (Random. seed)
      .nextGaussian
      (* sigma)
      (+ mu)))

(defn clip
  "Given an interval, values outside the interval are clipped to the interval edges."
  ^Double
  [^Double val ^Double lower ^Double upper]
  (-> val
      (max lower)
      (min upper)))

(defn rand-angle-steps
  "Generates the division of a circumference in random angles."
  ^clojure.lang.PersistentVector
  [^Long seed ^Integer steps ^Double irregularity]
  (let [lower (-> Math/PI (* 2) (/ steps) (- irregularity))
        upper (-> Math/PI (* 2) (/ steps) (+ irregularity))
        angles (map #(rand-seeded (+ seed %) lower upper) (range steps))
        cumulative (-> (reduce + angles) (/ (* 2 Math/PI)))]
    (map #(/ % cumulative) angles)))

(defn generate-polygon
  "Start with the center of the polygon at center, then creates the
   polygon by sampling points on a circle around the center.
   Random noise is added by varying the angular spacing between
   sequential points, and by varying the radial distance of each
   point from the centre."
  ^clojure.lang.PersistentVector
  [^Long seed
   ^clojure.lang.PersistentVector center
   ^Double avg-radius
   ^Double irregularity-in
   ^Double spikiness-in
   ^Integer num-vertices]
  (let [tau (* 2 Math/PI)
        irregularity (* irregularity-in (/ tau num-vertices))
        spikiness (* spikiness-in avg-radius)
        angle-steps (rand-angle-steps seed num-vertices irregularity)
        angle (rand-seeded seed 0.0 tau)]
    (->> angle-steps
         (reduce (fn [{:keys [sum-angle points]} cur]
                   (let [radius (-> (+ seed (count points))
                                    (rand-gauss-seeded avg-radius spikiness)
                                    (clip 0.0 (* 2 avg-radius)))
                         point [(-> (first center)
                                    (+ radius)
                                    (* (Math/cos sum-angle)))
                                (-> (last center)
                                    (+ radius)
                                    (* (Math/sin sum-angle)))]]
                     {:sum-angle (+ sum-angle cur)
                      :points (merge points point)}))
                 {:sum-angle angle
                  :points []})
         :points)))
