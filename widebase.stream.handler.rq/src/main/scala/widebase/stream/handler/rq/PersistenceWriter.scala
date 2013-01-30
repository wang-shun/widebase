package widebase.stream.handler.rq

import scala.collection.mutable.ArrayBuffer

import widebase.io.VariantWriter
import widebase.io.file.FileVariantWriter

/** A persistence writer.
 *
 * @author myst3r10n
 */
class PersistenceWriter(val partition: String) {

  class Companion(var writer: FileVariantWriter, var lastEnded: Long)

  var records = 0

  var label: FileVariantWriter = null
  val columns = ArrayBuffer[FileVariantWriter]()
  var symbolCompanions = Map[Int, Companion]()
  var stringCompanions = Map[Int, Companion]()

}

