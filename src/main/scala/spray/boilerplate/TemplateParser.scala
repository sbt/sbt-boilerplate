/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012 Johannes Rudolph
 */
package spray.boilerplate

import util.parsing.combinator.RegexParsers
import java.lang.RuntimeException

sealed trait TemplateElement
case class Sequence(elements: Seq[TemplateElement]) extends TemplateElement
/** A literal string that shouldn't be changed */
case class LiteralString(literal: String) extends TemplateElement
/* An offset to be replaced by the current index */
case class Offset(i: Int) extends TemplateElement
/** A region in which to apply expansions */
case class Expand(inner: TemplateElement, separator: String) extends TemplateElement

object TemplateParser extends RegexParsers {
  override type Elem = Char
  type Tokens = TemplateElement
  override val skipWhitespace = false

  def elements: Parser[TemplateElement] = rep1(element) ^^ {
    case one :: Nil => one
    case several => Sequence(several)
  }

  def element: Parser[TemplateElement] = offset| literalString | expand

  def offset: Parser[Offset] = "[012]".r ^^ (s => Offset(s.toInt))
  def literalString: Parser[LiteralString] = rep1(escapedLiteralNumber | literalChar) ^^ (chs => LiteralString(chs.mkString))
  def literalChar: Parser[Char] =
    not("[#" | """#[^\]]*\]""".r | "[012]".r) ~> elem("Any character", _ => true)

  def escapedLiteralNumber: Parser[Char] = "##" ~> """[012]""".r ^^ (_.head)

  def outsideTemplate: Parser[LiteralString]= """(?s).+?(?=(\[#)|(\z))""".r ^^ (LiteralString(_))

  def expand: Parser[Expand] = "[#" ~> elements ~ "#" ~ separatorChars <~ "]" ^^ {
    case els ~ x ~ sep => Expand(els, sep.getOrElse(", "))
  }
  def outsideElements: Parser[TemplateElement] =
    rep1(expand | outsideTemplate) ^^ {
      case one :: Nil => one
      case several => Sequence(several)
    }

  def separatorChars: Parser[Option[String]] = rep("""[^\]]""".r) ^^ (_.reduceLeftOption(_ + _))

  def parse(input:String): TemplateElement =
    phrase(outsideElements)(new scala.util.parsing.input.CharArrayReader(input.toCharArray)) match {
      case Success(res,_) => res
      case x:NoSuccess => throw new RuntimeException(x.msg)
    }
}

object TestParser extends App {
  def check(format: String) {
    println(TemplateParser.parse(format))
    println("Template:\n"+format+"\n")
    println("Generated Code:\n"+Generator.generateFromTemplate(format,5))
    println("-----End-----\n")
  }

  check("""This text
          |should not be parsed
          |Tuple1
          |
          [#Tuple1#]
          |
          |Product 1""".stripMargin)

  check("[#abc ##1 # ++ ]")
  check("[#abc Tuple##22 #]")
  check("[#abc Tuple1 #]")
}