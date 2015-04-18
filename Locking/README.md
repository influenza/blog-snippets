Locking Strategy Sample
==========================

This code is to follow along with the [companion blog post](http://dahlgren.so/software/2013/04/22/Locking-Strategies/).

Requirements
------------

1. [Gradle](https://gradle.org/)
2. JDK of your choice

Building
--------

      $ git clone https://github.com/influenza/blog-snippets.git
      $ cd blog-snippets/Locking
      $ gradle build
      $ java -jar ./build/libs/Locking.jar -accessmethod locks -duration 30 -readerthreads 13 -writerthreads 3
