package cc.spray.boilerplate

import sbt._
import Keys._

object BoilerplatePlugin extends Plugin {
  object Boilerplate {
    val boilerplateGenerate = TaskKey[Seq[File]]("boilerplate-generate", "Generates boilerplate from template files")

    val settings = seq(
      sourceDirectory in boilerplateGenerate <<= (sourceDirectory in Compile) / "boilerplate",

      target in boilerplateGenerate <<= (sourceManaged in Compile),

      boilerplateGenerate <<= (streams, sourceDirectory in boilerplateGenerate, target in boilerplateGenerate) map Generator.generateFromTemplates,

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
      sourceDir.descendantsExcept(filt, excl).get
  }
}
