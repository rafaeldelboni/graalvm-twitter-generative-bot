(ns rafaeldelboni.vertices-test
  (:require [clojure.test :refer [are deftest is testing]]
            [rafaeldelboni.vertices :as vertices]))

(deftest rand-seeded-test
  (testing "should return randon (seeded) numbers between range"
    (are [seed min max expected] (= expected (vertices/rand-seeded seed min max))
      ;seed min  max   expected
      1     5.0  5.0   5.0
      2     5.0  5.0   5.0
      1     5.0  10.0  8.654390953516454
      2     5.0  10.0  8.65573468009953
      2     5.0  100.0 74.45895892189105
      1     10.0 20.0  17.30878190703291)))

(deftest rand-gauss-seeded-test
  (testing "should return random Gaussian distribution with mean of 2 and standard deviation of 0.5"
    (is (= 2.7807905200944774
           (vertices/rand-gauss-seeded 1 2.0 0.5)))))

(deftest clip-test
  (testing "should clip args into low & max values"
    (are [val lower upper expected] (= expected (vertices/clip val lower upper))
      ;val lower upper expected
      6.0  5.0   10.0  6.0
      4.0  5.0   10.0  5.0
      11.0 5.0   10.0  10.0)))
