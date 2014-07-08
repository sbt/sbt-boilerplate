/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012 Johannes Rudolph
 */
package spray.boilerplate

import util.parsing.combinator.RegexParsers
import java.lang.RuntimeException

sealed trait TemplateElement {
  def ~(next: TemplateElement): TemplateElement = Sequence(this, next)
}
case class Sequence(elements: TemplateElement*) extends TemplateElement {
  override def ~(next: TemplateElement): TemplateElement = Sequence(elements :+ next: _*)
}
/** A literal string that shouldn't be changed */
case class LiteralString(literal: String) extends TemplateElement
/* An offset to be replaced by the current index */
case class Offset(i: Int) extends TemplateElement

case class Range(start: Option[Int] = None, end: Option[Int] = None)

/** A region in which to apply expansions */
case class Expand(
  inner: TemplateElement,
  separator: String = Expand.defaultSeparator,
  range: Range = Range(None, None)) extends TemplateElement
object Expand {
  val defaultSeparator = ", "
}

object TemplateParser extends RegexParsers {
  override type Elem = Char
  type Tokens = TemplateElement
  override val skipWhitespace = false

  def elements: Parser[TemplateElement] = rep1(element) ^^ maybeSequence
  def element: Parser[TemplateElement] = offset | literalString | expand

  def offset: Parser[Offset] = offsetChars ^^ (s ⇒ Offset(s.toInt))
  def literalString: Parser[LiteralString] = rep1(escapedLiteralNumber | literalChar) ^^ (chs ⇒ LiteralString(chs.mkString))
  def literalChar: Parser[Char] =
    not(expandStart | """#[^\]]*\]""".r | offsetChars) ~> elem("Any character", _ ⇒ true)

  def offsetChars = "[012]".r

  def escapedLiteralNumber: Parser[Char] = "##" ~> offsetChars ^^ (_.head)

  def outsideTemplate: Parser[LiteralString] = """(?s).+?(?=(\[#)|(\z))""".r ^^ (LiteralString(_))

  def expand: Parser[Expand] = expandStart ~ elements ~ "#" ~ separatorChars <~ "]" ^^ {
    case range ~ els ~ x ~ sep ⇒ Expand(els, sep.getOrElse(Expand.defaultSeparator), range)
  }
  def expandStart: Parser[Range] = "[" ~> range <~ "#"

  def range: Parser[Range] =
    (opt("""\d{1,2}""".r) ~ """\s*\.\.\s*""".r ~ opt("""\d{1,2}""".r) ^^ {
      case start ~ sep ~ end ⇒ Range(start.map(_.toInt), end.map(_.toInt))
    }) | success(Range())

  def outsideElements: Parser[TemplateElement] =
    rep1(expand | outsideTemplate) ^^ maybeSequence

  def separatorChars: Parser[Option[String]] = rep("""[^\]]""".r) ^^ (_.reduceLeftOption(_ + _))

  def maybeSequence(els: Seq[TemplateElement]): TemplateElement = els match {
    case one :: Nil ⇒ one
    case several    ⇒ Sequence(several: _*)
  }

  def parse(input: String): TemplateElement =
    phrase(outsideElements)(new scala.util.parsing.input.CharArrayReader(input.toCharArray)) match {
      case Success(res, _) ⇒ res
      case x: NoSuccess    ⇒ throw new RuntimeException(x.msg)
    }
}
