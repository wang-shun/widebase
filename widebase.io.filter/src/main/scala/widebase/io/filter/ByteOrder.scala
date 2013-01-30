package widebase.io.filter

/** Supported byte orders.
 *
 * @author myst3r10n
 */
object ByteOrder extends Enumeration {

  type ByteOrder = Value

  val BigEndian, LittleEndian, Native = Value

}

