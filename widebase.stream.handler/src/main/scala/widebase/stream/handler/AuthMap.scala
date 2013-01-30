package widebase.stream.handler

import java.io. { File, FileInputStream }
import java.util.Properties

import scala.collection.mutable.HashMap

/** Stores authorizations.
 *
 * Format:
 * {{{
 * message name / series of unique user or group
 * }}}
 *
 * @author myst3r10n
 */
class AuthMap extends HashMap[String, Array[String]] {

  var jaas = "widebase-stream"

}


/** Companion.
 *
 * @author myst3r10n
 */
object AuthMap {

  import scala.collection.JavaConversions._

  /** Load authorization map.
   *
   * @param filename of authorization map
   *
   * @return authorization map
   */
  def load(filename: String) = {

    val auths = new AuthMap
    val props = new Properties

    props.load(new FileInputStream(filename))

    enumerationAsScalaIterator(props.propertyNames).foreach {

      case message: String =>
        if(message == "JAAS")
          auths.jaas = props.getProperty(message)
        else
          auths += message -> props.getProperty(message).split(",")

    }

    auths

  }
}
