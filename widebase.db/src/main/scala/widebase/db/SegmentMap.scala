package widebase.db

import java.io.File

import scala.collection.mutable.LinkedHashMap

class SegmentMap extends LinkedHashMap[String, File] {

  def paths = values

}

object SegmentMap {

  def apply(elems: (String, File)) = {

    val segments = new SegmentMap
    segments += elems._1 -> elems._2
    segments

  }
}

