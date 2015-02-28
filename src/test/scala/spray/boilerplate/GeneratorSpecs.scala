package spray.boilerplate

import org.specs2.mutable.Specification

class GeneratorSpecs extends Specification {
  "Generation" should {
    "keep outer template unchanged" in {
      gen4("a1b2c3d4") === "a1b2c3d4"
    }
    "inflate 1 in expansion" in {
      gen4("[#a1#]") === "a1, a2, a3, a4"
    }
    "inflate 0 in expansion" in {
      gen4("[#a0#]") === "a0, a1, a2, a3"
    }
    "inflate 2 in expansion" in {
      gen4("[#a2#]") === "a2, a3, a4, a5"
    }
    "encode sharp" in {
      gen4("[#a\\#1#]") === "a#1, a#2, a#3, a#4"
    }
    "encode sharp2" in {
      gen4("[#a[]\\#1#]") === "a[]#1, a[]#2, a[]#3, a[]#4"
    }
    "encode sharp3" in {
      gen4("[#a[\\#]1#]") === "a[#]1, a[#]2, a[#]3, a[#]4"
    }
    "don't inflate when quoted in expansion" in {
      gen4("[#a1 ##1#]") === "a1 1, a2 1, a3 1, a4 1"
    }
    "inflate inner" in {
      gen4("[#a1([#T1#])#]") === "a1(T1), a2(T1, T2), a3(T1, T2, T3), a4(T1, T2, T3, T4)"
    }
    "support custom separator" in {
      gen4("[#a1#.]") === "a1.a2.a3.a4"
    }
    "support custom range start" in {
      gen4("[2..#a1#.]") === "a2.a3.a4"
    }
    "support custom range end" in {
      gen4("[..6#a1#.]") === "a1.a2.a3.a4.a5.a6"
    }
    "support custom range" in {
      gen4("[5..10#a1#.]") === "a5.a6.a7.a8.a9.a10"
    }
    "support inner custom range" in {
      gen4("[#a1([2..#T1#])#]") === "a1(), a2(T2), a3(T2, T3), a4(T2, T3, T4)"
    }
  }

  def gen4(template: String): String = Generator.generateFromTemplate(template, 4)
}
