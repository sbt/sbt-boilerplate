package spray.boilerplate

import org.specs2.mutable.Specification

class TemplateParserSpecs extends Specification {
  import TemplateParser.parse

  "TemplateParser.parse" should {
    "without expansion" in {
      parse("abc") === LiteralString("abc")
    }
    "just expansion" in {
      parse("[# def #]") === Expand(LiteralString(" def "), ", ")
    }
    "multiple expansions" in {
      parse("[#a#]abc[#b#]") ===
        Sequence(List(Expand(LiteralString("a"), ", "), LiteralString("abc"), Expand(LiteralString("b"), ", ")))
    }
    "one surrounded expansion" in {
      parse("abc[#a#]def") === Sequence(List(LiteralString("abc"), Expand(LiteralString("a"), ", "), LiteralString("def")))
    }
    "an expanded number" in {
      parse("[#T1#]") === Expand(Sequence(List(LiteralString("T"), Offset(1))), ", ")
    }
    "a quoted number" in {
      parse("[#T##1#]") === Expand(LiteralString("T1"), ", ")
    }
  }
}
