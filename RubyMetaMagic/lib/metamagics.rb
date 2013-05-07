# File to facilitate the learning of some level one Ruby Metaprogramming
module Metamagics
  # The secret sauce
  class Meta
    def metaclass
      class << self
        self
      end
    end
  end

  ###########################################################################
  # Start here! Take a look at each set of methods and throw down some code #
  # where you find 'your implementation here' comments!                     #
  ###########################################################################
  #     ___           __    ____           __
  #    / _ \___ _____/ /_  / __ \___  ___ / /
  #   / ___/ _ `/ __/ __/ / /_/ / _ \/ -_)_/ 
  #  /_/   \_,_/_/  \__/  \____/_//_/\__(_)  
  #                                         
  
  # Given a list of attribute symbols, return an object with getters and 
  # setters for each of them.
  # i.e., given read_write_attributes(:foo, :bar, :baz), the returned object should
  # have getters and setters for foo, bar, and baz attributes
  def self.read_write_attributes attr, *more
    result = Meta.new
    # Your implementation here
    result
  end

  # Given a list of name symbols, create a method for each that expects
  # two parameters and returns their sum.
  # i.e., given `obj = addition_method(:foo, :bar)`, the following
  # should hold true:
  #   obj.foo(1,1) == 2
  #   obj.bar(3,4) == 7
  def self.addition_method name, *more
    result = Meta.new
    # Your implementation here
    result
  end

  #     ___           __    ______            __
  #    / _ \___ _____/ /_  /_  __/    _____  / /
  #   / ___/ _ `/ __/ __/   / / | |/|/ / _ \/_/ 
  #  /_/   \_,_/_/  \__/   /_/  |__,__/\___(_)  
  #                                            

  # Given a list of name symbols, create attributes for each of them. Manually
  # implement an accessor such that any value that has been set is return in caps.
  # i.e., given `foo = make_angry_objects :greeting`, the following should hold
  # true:
  #   foo.greeting = "hi there!"
  #   foo.greeting == "HI THERE!"
  def self.make_angry_object name, *more
    result = Meta.new
    # Your implementation here
    result
  end

  # Given an attribute name and a list of acceptable values, create an object that
  # will raise an ArgumentError if the attribute is being set to a non-acceptable 
  # value.
  # i.e.,  given `foo = make_snobby_object :pen_type, ["Pilot G2"]`, the following
  # should hold true:
  #   foo.pen_type = "Pilot G2" # No problem! Everyone knows this is the best ink pen
  #   foo.pen_type = "Wally World Promo Pen" # <-- Raises ArgumentError
  def self.make_snobby_object attribute_name, acceptable_values 
    result = Meta.new
    # Your implementation here
    result
  end

  #     ___           __    ________               __
  #    / _ \___ _____/ /_  /_  __/ /  _______ ___ / /
  #   / ___/ _ `/ __/ __/   / / / _ \/ __/ -_) -_)_/ 
  #  /_/   \_,_/_/  \__/   /_/ /_//_/_/  \__/\__(_)  
  #                                                

  # Given a hash, turn all of the top level keys into attributes who's values
  # are those provided in the map. Don't worry about setters, we're only concerned
  # with getters here.
  # i.e., given `obj = transform_hash { :foo => "bar" }`, the following should hold
  # true:
  #   obj.foo == "bar"
  def self.transform_hash hash
    result = Meta.new
    # Your implementation here
    result
  end

  def self.lame_dsl attr_names, attr_values, &block
    result = Meta.new
    # Your meta magic here!
    # End meta magic
    # Ok.. you get a little help for this part
    begin
      result.instance_eval &block
    rescue ArgumentError # You are raising ArgumentError above, right?
      result = nil # NO OBJECT FOR YOU!
    end
    result
  end
end
