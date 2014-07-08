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
/** A string possibly containing patterns to replace */
case class LiteralString(literal: String) extends TemplateElement
/** A fixed string that shouldn't be changed */
case class FixedString(literal: String) extends TemplateElement
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

  def element: Parser[TemplateElement] = literal | fixed | expand

  def literalChar: Parser[String] = """(?s:(?!\[#)(?!#[^\]]*\]).)""".r
  def literalChars: Parser[String] = rep1(literalChar) ^^ { _.reduceLeft(_ + _) }

  def literal: Parser[LiteralString] = literalChars ^^ LiteralString

  def fixed: Parser[FixedString] = "##" ~> """\d+""".r ^^ (new String(_)) ^^ FixedString

  def outsideTemplate: Parser[FixedString]= """(?s).+?(?=(\[#)|(\z))""".r ^^ (FixedString(_))

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