(defun lagrange-interpolating-polynomial (points)
  "Return a symbolic expression of the Lagrange interpolating polynomial for a given set of points."
  (let ((terms
         (loop for i from 0 below (length points)
               for (xi yi) = (nth i points)
               collect
               `(* ,yi
                   ,(reduce (lambda (a b) `(* ,a ,b))
                            (loop for j from 0 below (length points)
                                  for (xj _) = (nth j points)
                                  when (/= i j)
                                  collect
                                  `(/ (- x ,xj) (- ,xi ,xj))))))))
    `(+ ,@terms)))

; (lagrange-interpolating-polynomial '((2 3) (4 4) (5 5) (7 4)))
; (setq poly (lagrange-interpolating-polynomial '((0 1.6) (0.5 3) (0.8 3.7) (1 4.8) (1.4 4.7) (2 3) (2.7 2.6))))


(defun mapconcat (fn lst sep)
  "Default macro in some lisp interpreters..."
  (apply #'concatenate 'string
         (let ((strings (mapcar fn lst)))
           (if strings
               (cons (car strings)
                     (mapcan (lambda (s) (list sep s)) (cdr strings)))
               '()))))


(defun p-print-gnuplot (expr)
  "Return a Gnuplot-compatible string of a symbolic math expression."
  (cond
    ((numberp expr) (princ-to-string expr))
    ((symbolp expr) (string-downcase (symbol-name expr))) ; assumes 'x' only
    ((listp expr)
     (let ((op (car expr))
           (args (cdr expr)))
       (case op
         (+ (mapconcat #'p-print-gnuplot args " + "))
         (- (if (= (length args) 1)
                (concatenate 'string "-(" (p-print-gnuplot (first args)) ")")
                (mapconcat #'p-print-gnuplot args " - ")))
         (* (mapconcat (lambda (e)
                         (let ((s (p-print-gnuplot e)))
                           (if (and (listp e)
                                    (not (eq (car e) '*)))
                               (concatenate 'string "(" s ")")
                               s)))
                       args
                       " * "))
         (/ (let ((num (p-print-gnuplot (first args)))
                  (den (p-print-gnuplot (second args))))
              (concatenate 'string "(" num ") / (" den ")")))
         (** (let ((base (p-print-gnuplot (first args)))
                   (exp (p-print-gnuplot (second args))))
               (concatenate 'string "(" base ")**(" exp ")")))
         (t (error "Unknown operator for Gnuplot: ~a" op)))))
    (t (error "Invalid expression: ~a" expr))))



(defun eval-expr (expr x)
  "Evaluate a symbolic Lisp expression, substituting x with the numeric value."
  (cond
    ((numberp expr) expr) ; base case: number
    ((symbolp expr) (if (eq expr 'x) x (error "Unknown symbol: ~a" expr)))
    ((listp expr)
     (let ((op (car expr))
           (args (cdr expr)))
       (case op
         (+ (apply #'+ (mapcar (lambda (e) (eval-expr e x)) args)))
         (- (apply #'- (mapcar (lambda (e) (eval-expr e x)) args)))
         (* (apply #'* (mapcar (lambda (e) (eval-expr e x)) args)))
         (/ (apply #'/ (mapcar (lambda (e) (eval-expr e x)) args)))
         (t (error "Unknown operator: ~a" op)))))
    (t (error "Invalid expression: ~a" expr))))
