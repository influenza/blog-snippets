require 'spec_helper'

describe VoteForMe::ArchetypePolitico do
  it 'repeats any question asked' do
    question = "What planet are you from?"
    candidate = VoteForMe::ArchetypePolitico.new 
    responses = candidate.question question
    puts responses
    expect(responses.first.downcase.start_with? question.downcase).to eq(true)
  end

  it 'regurgitates talking points when relevant' do
    candidate = VoteForMe::ArchetypePolitico.new
    jobs_man = VoteForMe::TalkingPoint.new(
      "I am focused on creating jobs!", 'job', 'work', 'unemployment'
    )
    candidate.this_is_important jobs_man
    responses = candidate.question "What about the unemployment levels?"
    responses.shift # Ditch non-informative question repetition
    expect(responses.first).to eq(jobs_man.body)
  end

  it 'disparages competition' do
    fail "todo!"
  end

  it 'handles embarassing questions' do
    fail "todo!"
  end

  it 'delivers a monologue as the default case' do
    fail "todo!"
  end
end
