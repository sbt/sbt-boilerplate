/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012-2016 Johannes Rudolph
 */
package spray.boilerplate

import sbt._
import Keys._
import sbt.internal.io.Source

object Compat {
  private val boilerplateSourceDirectories = settingKey[Seq[File]]("Directories containing boilerplate template sources.")
  private val inputFilter = """.*\.template""".r

  def allPaths(f: File) = f.allPaths

  def watchSourceSettings = Def.settings {
    Seq(
      watchSources in Defaults.ConfigGlobal ++= boilerplateSourceDirectories.value map { dir =>
        new Source(
          dir,
          new NameFilter {
            override def accept(name: String): Boolean = inputFilter.pattern.matcher(name).matches()
          },
          NothingFilter)
      }
    )
  }
}
