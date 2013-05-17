module VoteForMe
  # Instantiate for a new candidate!
  class ArchetypePolitico
    def init confidence = 1.0, frustration = 0.0
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
  end

  # The parent of all utterances made by an ArchetypePolitico
  class VapidStatement
    attr_reader :category
    attr_reader :body
    def init category, body = "umm..." # A perfect default!
      @category = category
    end
  end

  # Jobs! Accountability! Change!
  class TalkingPoint < VapidStatement
    def init body
      super :talking_point, body
    end
  end

  # Excited! Optimistic! Confident!
  class OptimisticPlatitude < VapidStatement
    def init body
      super :platitude, body
    end
  end

  # Upbringing! Family! Religion!
  class BioBlurb < VapidStatement
    def init body
      super :bio, body
    end
  end
end
