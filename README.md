# sbt-boilerplate

Boilerplate is an sbt-plugin that generates stubs for code which has to be expanded
for all numbers of arguments from 1 to 22. This is sometimes necessary to support
all of the `TupleX` or `FunctionX` generically.

The plugin defines a simple template language for this purpose.

## The template language

The template file contains mostly literal code with some control characters guiding the
expansion. Expansion follows these rules:

 - The current number of arguments `i` is initialized to 22.
 - Code embraced in `[#` and `#]` is copied `i` times and the expansion is applied
   recursively with `i` being set accordingly.
   - It is possible to define a custom separator
     between the copied instances by putting the separator text between the `#` and the `]` of the closing
     bracket. If no separator is supplied `", "` is assumed.
   - You can specify a custom range `i` should iterate through by placing a term of the form `<start>..<end>` between
     the starting `[` and `#` of an expansion. Either `start` or `end` can be omitted in which case the defaults are         assumed.
 - Everywhere digit `1` is replaced by `i`, digit `0` is replaced by `i - 1`, and digit `2` is replaced by `i + 1`
   unless the digit is prefixed with `##`.

## Examples

### Apply function to tuple

Consider the task is to provide overloads for a function which can apply a function to
a tuple for all numbers of arguments from 1 to 22.

Start by writing out the function for only one argument:

    def applyFunc[P1, R](input: Tuple1[P1], func: (P1) => R): R =
      func(input._1)

For the function to be copied for each possible number of arguments, enclose it in `[#`
and `#]` (the newline between the closing `#` and `]` defines that instances should be
separated by newline and not by the default `", "`):

    [#def applyFunc[P1, R](input: Tuple1[P1], func: (P1) => R): R =
      func(input._1)#
    ]

This will already expand to:

    def applyFunc[P1, R](input: Tuple1[P1], func: (P1) => R): R =
      func(input._1)
    def applyFunc[P2, R](input: Tuple2[P2], func: (P2) => R): R =
      func(input._2)
    def applyFunc[P3, R](input: Tuple3[P3], func: (P3) => R): R =
      func(input._3)
    def applyFunc[P4, R](input: Tuple4[P4], func: (P4) => R): R =
      func(input._4)
    // ...

This is not yet what we want, because we `P1` to expand to
`P1, P2, ..., Pi` and `input._1` to `input._1, input._2, ..., input._i`. So we embrace the
parts to expand another time:

    [#def applyFunc[[#P1#], R](input: Tuple1[[#P1#]], func: ([#P1#]) => R): R =
      func([#input._1#])#
    ]

This now expands correctly to

    def applyFunc[P1, R](input: Tuple1[P1], func: (P1) => R): R =
      func(input._1)
    def applyFunc[P1, P2, R](input: Tuple2[P1, P2], func: (P1, P2) => R): R =
      func(input._1, input._2)
    def applyFunc[P1, P2, P3, R](input: Tuple3[P1, P2, P3], func: (P1, P2, P3) => R): R =
      func(input._1, input._2, input._3)
    def applyFunc[P1, P2, P3, P4, R](input: Tuple4[P1, P2, P3, P4], func: (P1, P2, P3, P4) => R): R =
      func(input._1, input._2, input._3, input._4)

## Usage

Put

    addSbtPlugin("io.spray" % "sbt-boilerplate" % "0.5.9")

into your `plugins.sbt` and add

    Boilerplate.settings

to your `build.sbt`.

The templates have to be put into the `src/main/boilerplate` directory and the file name
must end with `.template`. The generated files will be put into the same hierarchy as they
appear in `src/main/boilerplate` with the `.template` extension stripped off.

## Known issues

 * The maximum number of arguments, 22, is hard-coded.
 * Instances for 0 arguments have to be supplied manually.

## Projects using sbt-boilerplate
 
 * [spray-routing](http://github.com/spray/spray) uses sbt-boilerplate to provide conversions to/from HLists
 * [product-collections](https://github.com/marklister/product-collections)

## License

Copyright (c) 2012-2014 Johannes Rudolph

Published under the [BSD 2-Clause License](http://www.opensource.org/licenses/BSD-2-Clause).
