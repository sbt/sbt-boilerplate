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
case class LiteralString(literal: String) extends TemplateElement
case class FixedString(literal: String) extends TemplateElement
case class Expand(start:Int, inner: TemplateElement, separator: String) extends TemplateElement

object TemplateParser extends RegexParsers {
  override type Elem = Char
  type Tokens = TemplateElement
  override val skipWhitespace = false

  def elements: Parser[TemplateElement] = rep1(element) ^^ {
    case one :: Nil => one
    case several => Sequence(several)
  }

  def element: Parser[TemplateElement] = literal | fixed | expand

  def literalChar: Parser[String] = """(?s:(?!\[[0-9]*#)(?!#[^\]]*\]).)""".r
  def literalChars: Parser[String] = rep1(literalChar) ^^ { _.reduceLeft(_ + _) }

  def literal: Parser[LiteralString] = literalChars ^^ LiteralString

  def fixed: Parser[FixedString] = "##" ~> ".".r ^^ (new String(_)) ^^ FixedString

  def expand: Parser[Expand] = "[" ~> startChars ~ "#" ~ elements ~ "#" ~ separatorChars <~ "]" ^^ {
    case start ~ els ~ x ~ sep => Expand(Integer.parseInt(start._1.getOrElse("1")), els, sep.getOrElse(", "))
  }
  
  def startChars: Parser[Option[String]] = rep("""[0-9]""".r) ^^ (_.reduceLeftOption(_ + _))

  def separatorChars: Parser[Option[String]] = rep("""[^\]]""".r) ^^ (_.reduceLeftOption(_ + _))

  def parse(input:String): TemplateElement =
    phrase(elements)(new scala.util.parsing.input.CharArrayReader(input.toCharArray)) match {
      case Success(res,_) => res
      case x:NoSuccess => throw new RuntimeException(x.msg)
    }
}

object TestParser extends App {
  def check(format: String) {
    println(TemplateParser.parse(format))
  }

  check("[#abc ##1 # ++ ]")
  check("[2#abc ##1 # ++ ]")
  check("[12#abc ##1 # ++ ]")
}