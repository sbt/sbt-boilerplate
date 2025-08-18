package spray.boilerplate

import java.io.File
import xsbti.FileConverter
import xsbti.VirtualFile

private[boilerplate] object BoilerplatePluginCompat {
  def toVirtualFile(file: File, converter: FileConverter): VirtualFile =
    converter.toVirtualFile(file.toPath)
}
