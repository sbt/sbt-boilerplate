/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012-2016 Johannes Rudolph
 */
package spray.boilerplate

import sbt._
import Keys._

object Compat {
  private val boilerplateSource = settingKey[File]("Default directory containing boilerplate template sources.")
  private val inputFilter = "*.template"

  def allPaths(f: File) = f.***

  def watchSourceSettings = Def.settings {
    Seq(watchSources in Defaults.ConfigGlobal ++= ((boilerplateSource.value ** inputFilter) --- (boilerplateSource.value ** excludeFilter.value ** inputFilter)).get)
  }
}
