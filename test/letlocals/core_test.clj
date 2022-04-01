(ns letlocals.core-test
  (:require [clojure.test :refer :all]
            [letlocals.core :refer :all]))

(deftest letlocal-no-bind-test
  (testing "should return last command"
    (is (= 100 (letlocals 100))))

  (testing "should run multiple commands"
    (let [vval (atom nil)]
      (is (= 100 (letlocals (reset! vval 100) @vval))))))

(deftest letlocal-destruct-test
  (testing "destruct map"
    (is (= [100 200]
           (letlocals
             (bind {:keys [a b]} {:a 100 :b 200})
             [a b]))))

  (testing "destruct list"
    (is (= [100 200]
           (letlocals
             (bind {:keys [a b]} {:a 100 :b 200})
             [a b])))))

(deftest letlocal-bind-test
  (testing "should return variable value"
    (is (= 100 (letlocals (bind b 100) b))))

  (testing "should return non variable"
    (is (= 100 (letlocals (bind b 99) 100))))

  (testing "should accept nested binds"
    (is (= [100 100] (letlocals (bind b 100) [b b]))))

  (testing "should run commands before bind"
    (let [vval (atom nil)]
      (is (= 100 (letlocals (reset! vval 100) (bind x 100) @vval)))))

  (testing "should run multiple binds"
    (let [vval (atom [])]
      (is (= [100 200] (letlocals
                         (bind x 100)
                         (swap! vval conj x)
                         (bind y 200)
                         (swap! vval conj y)))))))

(deftest letlocal-error-test
  (is (thrown-with-msg?
        IllegalStateException
        #"Invalid bind statement: \(bind x nil nil nil\)\nunnecessary options: \(nil nil\)"
        (validate-bind '(bind x nil nil nil))))

  (is (thrown-with-msg?
        IllegalStateException
        #"Invalid bind statement: \(letlocals \(bind 100 100\)\)\nvar value is missing"
        (validate-bind '(letlocals (bind 100 100)))))

  (is (thrown-with-msg?
        IllegalStateException
        #"Invalid bind statement: \(letlocals \(letlocals \(bind a\)\)\)\nvar value is missing"
        (validate-bind '(letlocals (letlocals (bind a))))))

  (is (thrown-with-msg?
        IllegalStateException
        #"Invalid bind statement: \(letlocals \(letlocals \(bind\)\)\)\nvar value is missing"
        (validate-bind '(letlocals (letlocals (bind))))))

  (is (thrown-with-msg?
        IllegalStateException
        #"Invalid bind statement: \(letlocals \(bind nil 100\)\)\nvar value is missing"
        (validate-bind '(letlocals (bind nil 100))))))

(deftest letlocal-macroexpand-test
  (is (= '(let* [a 100 b 100]
            (println a)
            (let* [c nil]
              (println b)))
         (clojure.walk/macroexpand-all
           '(letlocals
              (bind a 100)
              (bind b 100)
              (println a)
              (bind c nil)
              (println b))))))
