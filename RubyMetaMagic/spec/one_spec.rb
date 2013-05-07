require 'spec_helper'

describe Metamagics do
  it 'can define read-write attributes' do
    result = Metamagics.read_write_attributes(:foo, :bar, :baz)
    result.foo = "x"
    result.bar = "y"
    result.baz = "z"
    expect(result.foo).to eq("x")
    expect(result.bar).to eq("y")
    expect(result.baz).to eq("z")
  end

  it 'can define addition methods' do
    result = Metamagics.addition_method(:squeeze_two_numbers_into_one, :repeat_successor)
    expect(result.squeeze_two_numbers_into_one 3, 4).to eq(7)
    expect(result.repeat_successor 1, 4).to eq(5)
  end
end
