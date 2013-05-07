require 'spec_helper'

describe Metamagics do
  it 'can shout things back at me' do
    result = Metamagics.make_angry_object(:thoughts, :feelings, :emotions)
    # ... I am shocked that these three nouns are the same length
    result.thoughts = "A nice calm lake with a cool breeze"
    result.feelings = "I am completely calm and serene"
    result.emotions = "I am the epitome of relaxation"
    # Now bring me the irony!
    expect(result.thoughts).to eq("A NICE CALM LAKE WITH A COOL BREEZE")
    expect(result.feelings).to eq("I AM COMPLETELY CALM AND SERENE")
    expect(result.emotions).to eq("I AM THE EPITOME OF RELAXATION")
  end

  it 'can be pendantic about letting me set values' do
    result = Metamagics.make_snobby_object(:chosen_language, ["ruby", "scala", "erlang"])
    result.chosen_language = "ruby" # <3 you Ruby
    expect(result.chosen_language).to eq("ruby")
    # Sweet, we can set things. Now let's make sure I can't choose an abomination for a 
    # language.
    expect { result.chosen_language = "VBScript" }.to raise_error(ArgumentError)
  end
end
