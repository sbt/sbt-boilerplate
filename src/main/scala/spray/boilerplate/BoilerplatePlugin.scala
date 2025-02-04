/*
 * sbt-boilerplate is distributed under the 2-Clause BSD license. See the LICENSE file in the root
 * of the repository.
 *
 * Copyright (c) 2012-2016 Johannes Rudolph
 */
package spray.boilerplate

import java.io.FileInputStream

import sbt._
import Keys._
import sbt.plugins.JvmPlugin

object BoilerplatePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = noTrigger
  override def `requires`: Plugins = JvmPlugin

  object autoImport {
    val boilerplateGenerate = taskKey[Seq[File]]("Generates boilerplate from template files")
    val boilerplateSource = settingKey[File]("Default directory containing boilerplate template sources.")
    val boilerplateSignature = settingKey[String](
      "Function that creates signature string to prepend to the generated file (given an input file name). " +
        "Will be used to detect boilerplate-generated files")
    val boilerplateGeneratedExtension = settingKey[String]("Extension of generated source files")
  }

  import autoImport._

  override def projectSettings: Seq[Def.Setting[_]] =
    inConfig(Compile)(rawBoilerplateSettings) ++ inConfig(Test)(rawBoilerplateSettings) ++ Seq(
      boilerplateSignature := "// auto-generated by sbt-boilerplate\n")

  private def rawBoilerplateSettings: Seq[Setting[_]] = {
    Compat.watchSourceSettings ++
      Seq(
        boilerplateSource := sourceDirectory.value / "boilerplate",
        boilerplateGeneratedExtension := "scala",
        boilerplateGenerate := generateFromTemplates(streams.value, boilerplateSignature.value, boilerplateSource.value, sourceManaged.value, boilerplateGeneratedExtension.value),
        mappings in packageSrc ++= managedSources.value pair (Path.relativeTo(sourceManaged.value) | Path.flat),
        sourceGenerators += boilerplateGenerate)
  }

  def generateFromTemplates(streams: TaskStreams, signature: String, sourceDir: File, targetDir: File, extension: String): Seq[File] = {
    val files = sourceDir ** "*.template"
    streams.log.debug(s"Found ${files.get().size} template files in $sourceDir.")

    def changeExtension(f: File): File = {
      val (_, name) = f.getName.reverse.span(_ != '.')
      val strippedName = name.drop(1).reverse.toString
      val newName =
        if (!strippedName.contains(".")) s"$strippedName.$extension"
        else strippedName
      new File(f.getParent, newName)
    }

    val mapping = (files pair Path.rebase(sourceDir, targetDir)).map {
      case (orig, target) => (orig, changeExtension(target))
    }

    val newFiles = mapping.map(_._2)
    clearTargetDir(streams, targetDir, signature, newFiles)
    mapping foreach {
      case (templateFile, target) =>
        if (templateFile.lastModified > target.lastModified) {
          streams.log.info("Generating '%s'" format target.getName)
          val template = IO.read(templateFile)
          IO.write(target,
            signature + Generator.generateFromTemplate(template, 22))
        } else
          streams.log.debug("Template '%s' older than target. Ignoring." format templateFile.getName)
    }

    newFiles
  }

  def clearTargetDir(streams: TaskStreams, targetDir: File, signature: String, newFiles: Seq[File]): Seq[File] = {
    val fileSet = newFiles.toSet

    val buffer = new Array[Byte](signature.getBytes("utf8").size)
    def containsSignature(file: File): Boolean = {
      val f = new FileInputStream(file)
      try {
        val read = f.read(buffer)
        (read != buffer.length) || // if we haven't read the full signature assume we had read it
          new String(buffer, "utf8") == signature
      } finally f.close()
    }

    val toRemove =
      Compat.allPaths(targetDir)
        // apply filters with increasing effort
        .filter(f => f.exists && f.isFile)
        .filter(_.length >= signature.length)
        .filter(!fileSet(_))
        .filter(containsSignature _)
        .get()

    toRemove.foreach { f =>
      streams.log.debug(s"Removing $f that was formerly created by sbt-boilerplate (but won't be created anew).")
      f.delete
    }
    toRemove
  }
}
