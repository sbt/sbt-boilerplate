/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012 Johannes Rudolph
 */
package spray.boilerplate

import sbt._
import Keys._

object BoilerplatePlugin extends Plugin {
  object Boilerplate {
    val boilerplateGenerate = TaskKey[Seq[File]]("boilerplate-generate", "Generates boilerplate from template files")

    val settings = seq(
      sourceDirectory in boilerplateGenerate <<= (sourceDirectory in Compile) / "boilerplate",

      target in boilerplateGenerate <<= (sourceManaged in Compile),

      boilerplateGenerate <<= (streams, sourceDirectory in boilerplateGenerate, target in boilerplateGenerate) map generateFromTemplates,

      (sourceGenerators in Compile) <+= boilerplateGenerate,
      (managedSourceDirectories in Compile) <+= target in boilerplateGenerate,

      // watch sources support
      includeFilter in boilerplateGenerate := "*.template",
      excludeFilter in boilerplateGenerate <<= excludeFilter in Global,
      watch(sourceDirectory in boilerplateGenerate, includeFilter in boilerplateGenerate, excludeFilter in boilerplateGenerate),

      // add managed sources to the packaged sources
      mappings in (Compile, packageSrc) <++=
        (sourceManaged in Compile, managedSources in Compile) map { (base, srcs) =>
          (srcs x (Path.relativeTo(base) | Path.flat))
        }
    )

    def watch(sourceDirKey: SettingKey[File], filterKey: SettingKey[FileFilter], excludeKey: SettingKey[FileFilter]) =
      watchSources <++= (sourceDirKey, filterKey, excludeKey) map descendents
    def descendents(sourceDir: File, filt: FileFilter, excl: FileFilter) =
      descendantsExcept(sourceDir, filt, excl).get

    def generateFromTemplates(streams: TaskStreams, sourceDir: File, targetDir: File): Seq[File] = {
      val files = sourceDir ** "*.template"

      def changeExtension(f: File): File = {
        val (ext, name) = f.getName.reverse.span(_ != '.')
        new File(f.getParent, name.drop(1).reverse.toString)
      }

      val mapping = (files x rebase(sourceDir, targetDir)).map {
        case (orig, target) => (orig, changeExtension(target))
      }

      mapping foreach {
        case (templateFile, target) =>
          if (templateFile.lastModified > target.lastModified) {
            streams.log.info("Generating '%s'" format target.getName)
            val template = IO.read(templateFile)
            IO.write(target, Generator.generateFromTemplate(template, 22))
          } else
            streams.log.debug("Template '%s' older than target. Ignoring." format templateFile.getName)
      }

      mapping.map(_._2)
    }
  }

  def descendantsExcept(path: PathFinder, include: FileFilter, intermediateExclude: FileFilter): PathFinder =
    (path ** include) --- (path ** intermediateExclude ** include)
}
