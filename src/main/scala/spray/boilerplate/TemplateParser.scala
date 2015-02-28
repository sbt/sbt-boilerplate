/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012 Johannes Rudolph
 */
package spray.boilerplate

import scala.util.parsing.combinator.RegexParsers

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

  val EOI = 26.toChar

  lazy val elements: Parser[TemplateElement] = rep1(element) ^^ maybeSequence
  lazy val element: Parser[TemplateElement] = literalString | offset | expand

  lazy val offset: Parser[Offset] = offsetChars ^^ (s ⇒ Offset(s.toInt))
  lazy val literalString: Parser[LiteralString] = rep1(escapedSharp | escapedLiteralNumber | literalChar) ^^ (chs ⇒ LiteralString(chs.mkString))
  lazy val literalChar: Parser[Char] =
    not(expandStart | """#[^\]]*\]""".r | offsetChars) ~> elem("Any character", _ != EOI)

  lazy val offsetChars = "[012]".r

  lazy val escapedSharp: Parser[Char] = "\\#" ^^ (_.drop(1).head)
  lazy val escapedLiteralNumber: Parser[Char] = "##" ~> offsetChars ^^ (_.head)

  lazy val outsideLiteralString: Parser[LiteralString] = rep1(escapedSharp | outsideLiteralChar) ^^ (chs ⇒ LiteralString(chs.mkString))
  lazy val outsideLiteralChar: Parser[Char] = not(expandStart) ~> elem("Any character", _ != EOI)

  lazy val expand: Parser[Expand] = expandStart ~ elements ~ "#" ~ separatorChars <~ "]" ^^ {
    case range ~ els ~ x ~ sep ⇒ Expand(els, sep.getOrElse(Expand.defaultSeparator), range)
  }
  lazy val expandStart: Parser[Range] = "[" ~> range <~ "#"

  lazy val range: Parser[Range] =
    opt("""\d{1,2}""".r) ~ opt("""\s*\.\.\s*""".r) ~ opt("""\d{1,2}""".r) ^^ {
      case None ~ None ~ None       ⇒ Range()
      case start ~ Some("..") ~ end ⇒ Range(start.map(_.toInt), end.map(_.toInt))
    } //) | success(Range())

  lazy val outsideElements: Parser[TemplateElement] =
    rep1(expand | outsideLiteralString) ^^ maybeSequence

  lazy val separatorChars: Parser[Option[String]] = rep("""[^\]]""".r) ^^ (_.reduceLeftOption(_ + _))

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
