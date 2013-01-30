package widebase.util

import java.nio.charset.Charset

import widebase.io.filter. { ByteOrder, CompressionLevel, StreamFilter }

/** System properties.
 *
 * @author myst3r10n
 */
object SysProps {

  import widebase.io.filter.ByteOrder.ByteOrder
  import widebase.io.filter.StreamFilter.StreamFilter

  /** Get system property of [[java.lang.String]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def get(key: String, default: String) = System.getProperty(key, default)

  /** Get system property of [[scala.Int]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def getInt(key: String, default: Int): Int = {

    val property = Integer.getInteger(key)

    if(property == null)
      return default

    property

  }

  /** Get system property of [[java.nio.charset.Charset]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def getCharset(key: String, default: Charset): Charset = {

    val property = System.getProperty(key)

    if(property == null)
      return default

    Charset.forName(property)

  }

  /** Get system property of [[widebase.io.filter.StreamFilter]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def getFilter(key: String, default: StreamFilter): StreamFilter = {

    val property = System.getProperty(key)

    if(property == null)
      return default

    StreamFilter.withName(property)

  }

  /** Get system property of [[widebase.io.filter.CompressionLevel]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def getLevel(key: String, default: Int): Int = {

    val property = Integer.getInteger(key)

    if(property == null)
      return default

    property

  }

  /** Get system property of [[widebase.io.filter.ByteOrder]].
   *
   * @param key of property
   * @param default property
   *
   * @return system property or default value
   */
  def getOrder(key: String, default: ByteOrder = ByteOrder.Native): ByteOrder = {

    val property = System.getProperty(key)

    if(property == null)
      return default

    ByteOrder.withName(property)

  }

  /** Set system property of [[java.lang.String]].
   *
   * @param key of property
   * @param value of property
   */
  def set(key: String, value: String) {

    System.setProperty(key, value)

  }

  /** Set system property of [[scala.Int]].
   *
   * @param key of property
   * @param value of property
   */
  def setInt(key: String, value: Int) {

    System.setProperty(key, value.toString)

  }

  /** Set system property of [[java.nio.charset.Charset]].
   *
   * @param key of property
   * @param value of property
   */
  def setCharset(key: String, value: Charset) {

    System.setProperty(key, value.displayName)

  }

  /** Set system property of [[widebase.io.filter.StreamFilter]].
   *
   * @param key of property
   * @param value of property
   */
  def setFilter(key: String, value: StreamFilter) {

    System.setProperty(key, value.toString)

  }

  /** Set system property of [[widebase.io.filter.CompressionLevel]].
   *
   * @param key of property
   * @param value of property
   */
  def setLevel(key: String, value: Int) {

    System.setProperty(key, value.toString)

  }

  /** Set system property of [[widebase.io.filter.ByteOrder]].
   *
   * @param key of property
   * @param value of property
   */
  def setOrder(key: String, value: ByteOrder) {

    System.setProperty(key, value.toString)

  }
}

