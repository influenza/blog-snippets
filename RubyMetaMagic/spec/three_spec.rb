require 'spec_helper'

describe Metamagics do
  it 'can magically turn my hashes into objects' do
    hash = {
      :height => "8'3\"",
      :weight => "375 lbs",
      :demeanor => "Enraged Mutant"
    }
    result = Metamagics.transform_hash(hash)
    expect(result.height).to eq("8'3\"")
    expect(result.weight).to eq("375 lbs")
    expect(result.demeanor).to eq("Enraged Mutant")
  end

  it 'can make a lame dsl' do
    attr_names = [:flavor, :skill]
    attr_values = [:terrible, :poor, :ok, :great]
    result = Metamagics.lame_dsl(attr_names, attr_values) do
      flavor :poor
      skill :terrible
    end
    result.should_not be_nil
    # Now try again with some forbidden values
    result = Metamagics.lame_dsl(attr_names, attr_values) do
      flavor :FANTASTIC
      skill :still_terrible_unfortunately
    end
    result.should be_nil
  end
end
