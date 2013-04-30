Autoincrement isn't Atomic
===========================

This is a small test to demonstrate the the auto-increment operator isn't atomic in java.

To verify this on your machine, perform the following:
```
$ gradle jar
$ java -jar build/libs/AutoincrementAtomicity-1.0.jar 4 1000
```

The two command line arguments specify the number of threads (arg 1) and number of iterations
(arg 2). You can vary these and see that there isn't really a connection between them and the
failure rate, provided the thread count is greater than 1.
