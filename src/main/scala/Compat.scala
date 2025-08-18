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
import spray.boilerplate.BoilerplatePluginCompat._

object Compat {
  private val boilerplateSource = settingKey[File]("Default directory containing boilerplate template sources.")
  private val inputFilter = """.*\.template""".r

  def allPaths(f: File) = f.allPaths

  def watchSourceSettings = Def.settings {
    Seq(
      Defaults.ConfigZero / watchSources += Def.uncached(
        new Source(
          boilerplateSource.value,
          new NameFilter {
            override def accept(name: String): Boolean = inputFilter.pattern.matcher(name).matches()
          },
          NothingFilter)
      )
    )
  }
}
