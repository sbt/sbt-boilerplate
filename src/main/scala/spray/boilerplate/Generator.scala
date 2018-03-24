/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012 Johannes Rudolph
 */
package spray.boilerplate

object Generator {
  def generateFromTemplate(template: String, expandTo: Int): String =
    generate(TemplateParser.parse(template))(expandTo)

  def generate(format: TemplateElement)(idx: Int): String = format match {
    case Sequence(els @ _*)        ⇒ els.map(e ⇒ generate(e)(idx)).mkString
    case Skip(inner, range)        ⇒ if (range.start.getOrElse(1) to range.end.getOrElse(idx) contains idx) "" else generate(inner)(idx)
    case Expand(inner, sep, range) ⇒ (range.start.getOrElse(1) to range.end.getOrElse(idx)).map(generate(inner)).mkString(sep)
    case Offset(i)                 ⇒ (idx + i - 1).toString
    case LiteralString(lit)        ⇒ lit
  }
}
