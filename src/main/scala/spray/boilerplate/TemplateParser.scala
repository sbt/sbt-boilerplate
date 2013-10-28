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

  def fixed: Parser[FixedString] = "##" ~> ".".r ^^ (new String(_)) ^^ FixedString
  
  def nonTemplate: Parser[FixedString]= """(?s).*?.(?=(\[#)|(\Z))""".r ^^ FixedString  

  def expand: Parser[Expand] = "[#" ~> elements ~ "#" ~ separatorChars <~ "]" ^^ {
    case els ~ x ~ sep => Expand(els, sep.getOrElse(", "))
  }
  def chunk:Parser[TemplateElement] =  expand | nonTemplate //order sensitive
  
  def embeddedTemplate:Parser[TemplateElement] = rep1(chunk)^^ {
    case one::Nil => one
    case several => Sequence(several)  
  }
  
  def separatorChars: Parser[Option[String]] = rep("""[^\]]""".r) ^^ (_.reduceLeftOption(_ + _))

  def parse(input:String): TemplateElement =
    phrase(embeddedTemplate)(new scala.util.parsing.input.CharArrayReader(input.toCharArray)) match {
      case Success(res,_) => res
      case x:NoSuccess => {println ("Compiler choked with input remaining: line:"+x.next.pos.line+" Col:"+x.next.pos.column)
                           throw new RuntimeException(x.msg)}
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
          |Product 1
          |[#second2 bite#]""".stripMargin)
  
  check("[#abc ##1 # ++ ]")
  check("pre stuff [#abc Tuple##22 #]")
  check("[#abc Tuple1 #] post stuff")
}