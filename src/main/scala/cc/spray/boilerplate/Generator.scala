package cc.spray.boilerplate

import util.parsing.combinator.RegexParsers

object Generator {
  def generateFromTemplate(template: String, expandTo: Int): String =
    generate(TemplateParser.parse(template))(expandTo)

  def formatNum0(num: Int) = num.formatted("%d")
  def formatNumSpace(num: Int) = num.formatted("%d")
  def replaceInPattern(pattern: String)(idx: Int): String =
           // in likely identifiers replace by '04' etc.
    pattern.replaceAll("(?<=\\w)(?<!\\d)(?<!##)1(?!\\d)", formatNum0(idx))
           .replaceAll("(?<=\\w)(?<!\\d)(?<!##)0(?!\\d)", formatNum0(idx - 1))

           // in other places replace by ' 4' etc.
           .replaceAll("(?<!\\w)(?<!\\d)(?<!##)1(?!\\d)", formatNumSpace(idx))
           .replaceAll("(?<!\\w)(?<!\\d)(?<!##)0(?!\\d)", formatNumSpace(idx - 1))

  def generate(format: TemplateElement)(idx: Int): String = format match {
    case Sequence(els) => els.map(e => generate(e)(idx)).mkString
    case Expand(inner, sep) => (1 to idx).map(generate(inner)).mkString(sep)
    case LiteralString(lit) => replaceInPattern(lit)(idx)
    case FixedString(lit) => lit
  }
}
