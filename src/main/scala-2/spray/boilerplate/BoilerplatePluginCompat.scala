package spray.boilerplate

import java.io.File
import xsbti.FileConverter

private[boilerplate] object BoilerplatePluginCompat {
  def toVirtualFile(file: File, converter: FileConverter): File =
    file

  implicit class DefOps(private val self: sbt.Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
