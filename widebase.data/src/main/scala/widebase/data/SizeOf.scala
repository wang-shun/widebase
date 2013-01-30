package widebase.data

/** Contains size of each supported [[widebase.data.Datatype]]s in bytes.
 *
 * @author myst3r10n
 */
object SizeOf {

  val bool = byte
  val byte = java.lang.Byte.SIZE / java.lang.Byte.SIZE
  val char = java.lang.Character.SIZE / java.lang.Byte.SIZE
  val double = java.lang.Double.SIZE / java.lang.Byte.SIZE
  val float = java.lang.Float.SIZE / java.lang.Byte.SIZE
  val int = java.lang.Integer.SIZE / java.lang.Byte.SIZE
  val long = java.lang.Long.SIZE / java.lang.Byte.SIZE
  val short = java.lang.Short.SIZE / java.lang.Byte.SIZE
  val month = int + byte
  val date = int
  val minute = int
  val second = int
  val time = int
  val dateTime = long
  val timestamp = long + int

}

