# Ruby Meta Magic, HOOOOOO!

This project provides a play-along inspired scaffolding for learning
some basic Ruby metaprogramming tricks.

It's the companion to [this blog post](http://dahlgren.so/software/2013/05/06/Ruby-Metamagic/),
so check that out for all the learnin' goodness.

### Setup
Once you've grabbed the code, you can use [bundler](http://gembundler.com) to ensure that you
have the required dependencies (which is just rake and rspec for now).

```
$ cd blog-snippets/RubyMetaMagic
$ bundle install
```

At this point you can begin using `rake` to run the test specs. By default, rake will try
to run all of the tests. In order to just run a sub-set, as is done in the blog post linked
above, use:
```
$ rake round_one
```

There are also `round_two` and `round_three` tasks available.

