package widebase

import java.io.File

import scala.collection.mutable.ArrayBuffer

import scala.xml.XML

/** Database package.
 *
 * @author myst3r10n
 */
package object db {

  /** Create [[widebase.db.Database]] instance.
   *
   * @param [[widebase.db.Database]]
   */
  def instance(path: String): Database = {

    if(!(new File(path)).isDirectory)
      throw DatabaseNotFoundException(path)

    val file = new File(path + "/segments.xml")

    var segments = new SegmentMap

    if(file.exists) {

      (XML.load(file.getPath) \ "segment").foreach(segment =>
        segments += (segment \ "@key").text -> new File(segment.text))

    }

    new Database(path, segments)

  }
}

