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
  private val boilerplateSourceDirectories = settingKey[Seq[File]]("Directories containing boilerplate template sources.")
  private val inputFilter = "*.template"

  def allPaths(f: File) = f.***

  def watchSourceSettings = Def.settings {
    Seq(watchSources in Defaults.ConfigGlobal ++= ((boilerplateSourceDirectories.value ** inputFilter) --- (boilerplateSourceDirectories.value ** excludeFilter.value ** inputFilter)).get)
  }
}
