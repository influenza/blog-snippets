module VoteForMe
  # Instantiate for a new candidate!
  class ArchetypePolitico

    def initialize confidence = 1.0, frustration = 0.0 
      # Normalized confidence and frustration values
      @confidence = confidence
      @frustration = frustration
      # The important stuff from my puppeteers!
      @talking_points = []
      # Anything else I can use to instill confidence
      @other_statements = []
    end

    def this_is_important talkingPoint
      @talking_points << talkingPoint
    end

    def say_this_too vapidStatement
      @other_statements << vapidStatement
    end

    def question questionBody
      # Downcase for ease of parsing
      downcased = questionBody.downcase
      # Determine if this contains any talking point keywords
      talkingPointMatches = @talking_points.find_all do |point|
        point.tags.reduce(false) do |acc, tag| 
          acc || (not downcased.index(tag).nil?)
        end
      end
      response = [questionBody + " Well..."]
      response += talkingPointMatches.map do |point| point.body end
      response
    end
  end

  # The parent of all utterances made by an ArchetypePolitico
  class VapidStatement
    attr_reader :category
    attr_reader :body
    def initialize category, body = "umm..." # A perfect default!
      @category = category
      @body = body
    end
  end

  # Jobs! Accountability! Change!
  class TalkingPoint < VapidStatement
    attr_accessor :tags
    def initialize body, tag, *more
      super :talking_point, body
      @tags = (more << tag)
    end
  end

  # Excited! Optimistic! Confident!
  class OptimisticPlatitude < VapidStatement
    def initialize body
      super :platitude, body
    end
  end

  # Upbringing! Family! Religion!
  class BioBlurb < VapidStatement
    def initialize body
      super :bio, body
    end
  end
end
