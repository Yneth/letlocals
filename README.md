# letlocals

Missing part of clojure

## Usage

Tired of writing, the following?

```clojure
(let [a (mk-a-thing)
      _ (do-something! a)
      b (mk-another-thing)]
  (is (= (foo b) (bar a))))
```

Then use `letlocals`!

```clojure
(letlocals
  (bind a (mk-a-thing))
  (do-something! a)
  (bind b (mk-another-thing))
  (is (= (foo b) (bar a))))
```

## License

Release under the MIT license. See LICENSE for the full license.
