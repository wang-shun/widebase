package widebase.db.column

import java.nio.channels.FileChannel

import scala.collection.mutable.WrappedArray

import vario.data.Datatype
import vario.data.Datatype.Datatype
import vario.file.FileVariantMapper
import vario.filter.MapFilter

/** Implements a typed column.
 *
 * @param t type of column
 * @param m mapper of file
 * @param r records of mapper
 * @param c channel of companion
 *
 * @author myst3r10n
 */
class TypedColumn(t: Datatype)
  (implicit
    m: FileVariantMapper = null,
    r: Int = 0,
    c: FileChannel = null) {

  import vario.data

  /** Mapper of file. */
  protected val mapper = m

  /** Records of mapper. */
  protected val records = r

  /** Channel of companion. */
  protected val channel = c

  protected var _typeOf = t

  /** Current type of column. */
  def typeOf = _typeOf

  /** Sets current type of column.
   *
   * @param replace type
   */
  protected def typeOf_=(replace: Datatype) {

    _typeOf = replace

  }

  {

    if(mapper != null && channel == null)
      mapper.mode = typeOf

  }

  /** A [[widebase.db.column.TypedHybridBuffer]] for fixed-length datatypes.
   *
   * @author myst3r10n
   */
  protected trait HybridFixedBuffer[A] extends TypedHybridBuffer[A] {

    protected val mapper = TypedColumn.this.mapper
    protected val capacity = TypedColumn.this.records

    override def +=(value: A) = {

      wasUntyped(Datatype.withValue(value))
      super.+=(value)

      this

    }

    /** Append values of other hybrid buffer into this hybrid buffer.
     *
     * @param other hybrid buffer to append
     */
    def ++=(other: TypedHybridBuffer[A]) = {

      wasUntyped(other.typeOf)
      super.++=(other)

      this

    }

    override def insert(n: Int, value: A) {

      wasUntyped(Datatype.withValue(value))
      super.insert(n, value)

    }

    /** Inserts values of other hybrid buffer into this hybrid buffer.
     *
     * @param n position
     * @param other hybrid buffer to insert
     */
    def insertAll(n: Int, other: TypedHybridBuffer[A]) {

      wasUntyped(other.typeOf)
      super.insertAll(n, other)

    }
  }

  /** Column of [scala.Boolean].
   *
   * @author myst3r10n
   */
  object bools extends HybridFixedBuffer[Boolean] {

    protected val sizeOf = data.sizeOf.bool

    val typeOf = Datatype.Bool

    protected def read = mapper.readBool
    protected def write(value: Boolean) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Byte].
   *
   * @author myst3r10n
   */
  object bytes extends HybridFixedBuffer[Byte] {

    protected val sizeOf = data.sizeOf.byte

    val typeOf = Datatype.Byte

    protected def read = mapper.read
    protected def write(value: Byte) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Char].
   *
   * @author myst3r10n
   */
  object chars extends HybridFixedBuffer[Char] {

    protected val sizeOf = data.sizeOf.char

    val typeOf = Datatype.Char

    protected def read = mapper.readChar
    protected def write(value: Char) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Double].
   *
   * @author myst3r10n
   */
  object doubles extends HybridFixedBuffer[Double] {

    protected val sizeOf = data.sizeOf.double

    val typeOf = Datatype.Double

    protected def read = mapper.readDouble
    protected def write(value: Double) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Float].
   *
   * @author myst3r10n
   */
  object floats extends HybridFixedBuffer[Float] {

    protected val sizeOf = data.sizeOf.float

    val typeOf = Datatype.Float

    protected def read = mapper.readFloat
    protected def write(value: Float) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Int].
   *
   * @author myst3r10n
   */
  object ints extends HybridFixedBuffer[Int] {

    protected val sizeOf = data.sizeOf.int

    val typeOf = Datatype.Int

    protected def read = mapper.readInt
    protected def write(value: Int) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Long].
   *
   * @author myst3r10n
   */
  object longs extends HybridFixedBuffer[Long] {

    protected val sizeOf = data.sizeOf.long

    val typeOf = Datatype.Long

    protected def read = mapper.readLong
    protected def write(value: Long) {

      mapper.write(value)

    }
  }

  /** Column of [scala.Short].
   *
   * @author myst3r10n
   */
  object shorts extends HybridFixedBuffer[Short] {

    protected val sizeOf = data.sizeOf.short

    val typeOf = Datatype.Short

    protected def read = mapper.readShort
    protected def write(value: Short) {

      mapper.write(value)

    }
  }

  /** Appends a single value to this column.
   *
   * @param value to append
   *
   * @return the column itself
   */
  def +=(value: Any) = {

    value match {
      case value: Boolean => bools += value
      case value: Byte => bytes += value
      case value: Char => chars += value
      case value: Double => doubles += value
      case value: Float => floats += value
      case value: Int => ints += value
      case value: Long => longs += value
      case value: Short => shorts += value

    }

    this

  }

  /** Appends all values of column to this column.
   *
   * @param column values to append
   *
   * @return the column itself
   */
  def ++=(column: TypedColumn) = {

    column.typeOf match {

      case Datatype.Bool => bools ++= column.bools
      case Datatype.Byte => bytes ++= column.bytes
      case Datatype.Char => chars ++= column.chars
      case Datatype.Double => doubles ++= column.doubles
      case Datatype.Float => floats ++= column.floats
      case Datatype.Int => ints ++= column.ints
      case Datatype.Long => longs ++= column.longs
      case Datatype.Short => shorts ++= column.shorts

    }

    this

  }

  /** Removes a single value from this column.
   *
   * @param value to remove
   *
   * @return the column itself
   */
  def -=(value: Any) = {

    value match {
      case value: Boolean => bools -= value
      case value: Byte => bytes -= value
      case value: Char => chars -= value
      case value: Double => doubles -= value
      case value: Float => floats -= value
      case value: Int => ints -= value
      case value: Long => longs -= value
      case value: Short => shorts -= value

    }

    this

  }

  /** Removes all values of column from this column.
   *
   * @param column values to remove
   *
   * @return the column itself
   */
  def --=(column: TypedColumn) = {

    column.typeOf match {

      case Datatype.Bool => bools --= column.bools
      case Datatype.Byte => bytes --= column.bytes
      case Datatype.Char => chars --= column.chars
      case Datatype.Double => doubles --= column.doubles
      case Datatype.Float => floats --= column.floats
      case Datatype.Int => ints --= column.ints
      case Datatype.Long => longs --= column.longs
      case Datatype.Short => shorts --= column.shorts

    }

    this

  }

  /** Selects an value by its index in the column.
   *
   * @param idx the index to select
   *
   * @return the value of this column at index idx, where 0 indicates the first value
   */
  def apply(idx: Int): Any =
    typeOf match {

      case Datatype.Bool => bools(idx)
      case Datatype.Byte => bytes(idx)
      case Datatype.Char => chars(idx)
      case Datatype.Double => doubles(idx)
      case Datatype.Float => floats(idx)
      case Datatype.Int => ints(idx)
      case Datatype.Long => longs(idx)
      case Datatype.Short => shorts(idx)

    }

  /** Clear column */
  def clear {

    typeOf match {

      case Datatype.Bool => bools.clear
      case Datatype.Byte => bytes.clear
      case Datatype.Char => chars.clear
      case Datatype.Double => doubles.clear
      case Datatype.Float => floats.clear
      case Datatype.Int => ints.clear
      case Datatype.Long => longs.clear
      case Datatype.Short => shorts.clear

    }
  }

  /** Foreach... */
  def foreach[U](f: Any => U) =
    typeOf match {

      case Datatype.Bool => bools.foreach(f)
      case Datatype.Byte => bytes.foreach(f)
      case Datatype.Char => chars.foreach(f)
      case Datatype.Double => doubles.foreach(f)
      case Datatype.Float => floats.foreach(f)
      case Datatype.Int => ints.foreach(f)
      case Datatype.Long => longs.foreach(f)
      case Datatype.Short => shorts.foreach(f)

    }

  /** Selects the first value of this column. */
  def head: Any =
    typeOf match {

      case Datatype.Bool => bools.head
      case Datatype.Byte => bytes.head
      case Datatype.Char => chars.head
      case Datatype.Double => doubles.head
      case Datatype.Float => floats.head
      case Datatype.Int => ints.head
      case Datatype.Long => longs.head
      case Datatype.Short => shorts.head

    }

  /** Inserts new value at a given index into this column.
   *
   * @param n the index where new value is inserted
   * @param value to insert
   */
  def insert(n: Int, value: Any) {

    value match {

      case value: Boolean => bools.insert(n, value)
      case value: Byte => bytes.insert(n, value)
      case value: Char => chars.insert(n, value)
      case value: Double => doubles.insert(n, value)
      case value: Float => floats.insert(n, value)
      case value: Int => ints.insert(n, value)
      case value: Long => longs.insert(n, value)
      case value: Short => shorts.insert(n, value)

    }
  }

  /** Inserts values of a column at a given index into this column.
   *
   * @param n the index where new values are inserted
   * @param column values to insert
   */
  def insertAll(n: Int, column: TypedColumn) {

    column.typeOf match {

      case Datatype.Bool => bools.insertAll(n, column.bools)
      case Datatype.Byte => bytes.insertAll(n, column.bytes)
      case Datatype.Char => chars.insertAll(n, column.chars)
      case Datatype.Double => doubles.insertAll(n, column.doubles)
      case Datatype.Float => floats.insertAll(n, column.floats)
      case Datatype.Int => ints.insertAll(n, column.ints)
      case Datatype.Long => longs.insertAll(n, column.longs)
      case Datatype.Short => shorts.insertAll(n, column.shorts)

    }
  }

  /** Selects the last value of this column. */
  def last: Any =
    typeOf match {

      case Datatype.Bool => bools.last
      case Datatype.Byte => bytes.last
      case Datatype.Char => chars.last
      case Datatype.Double => doubles.last
      case Datatype.Float => floats.last
      case Datatype.Int => ints.last
      case Datatype.Long => longs.last
      case Datatype.Short => shorts.last

    }

  /** The length of this resizable column.
   *
   * @return the number of values in this column
   */
  def length = 
    typeOf match {

      case Datatype.None => 0
      case Datatype.Bool => bools.length
      case Datatype.Byte => bytes.length
      case Datatype .Char => chars.length
      case Datatype.Double => doubles.length
      case Datatype.Float => floats.length
      case Datatype.Int => ints.length
      case Datatype.Long => longs.length
      case Datatype.Short => shorts.length

    }

  /** Replaces value at given index with a new value.
   *
   * @param n the index of the value to replace
   * @param replace value
   */
  def update(n: Int, replace: Any) {

    replace match {
      case replace: Boolean => bools(n) = replace
      case replace: Byte => bytes(n) = replace
      case replace: Char => chars(n) = replace
      case replace: Double => doubles(n) = replace
      case replace: Float => floats(n) = replace
      case replace: Int => ints(n) = replace
      case replace: Long => longs(n) = replace
      case replace: Short => shorts(n) = replace

    }
  }

  override protected def finalize {

    if(channel != null && typeOf == Datatype.String)
      channel.close

    super.finalize

  }

  /** Checks whether column was already typed.
   *
   * @param valueType self-explanatory
   */
  protected def wasUntyped(valueType: Datatype) {

    if(valueType == typeOf)
      return

    if(typeOf == Datatype.None) {

      typeOf = valueType
      return

    }

    throw MixedTypeException(typeOf, valueType)

  }
}

/** Companion of [[widebase.db.column.TypedColumn]].
 *
 * @author myst3r10n
 */
object TypedColumn {

  /** Creates [[widebase.db.column.TypedColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Any*) =
    new TypedColumn(Datatype.None) {

      typeOf = Datatype.withValue(values.head)

      values.head match {

        case value: Boolean => bools ++= values.asInstanceOf[WrappedArray[Boolean]]
        case value: Byte => bytes ++= values.asInstanceOf[WrappedArray[Byte]]
        case value: Char => chars ++= values.asInstanceOf[WrappedArray[Char]]
        case value: Double => doubles ++= values.asInstanceOf[WrappedArray[Double]]
        case value: Float => floats ++= values.asInstanceOf[WrappedArray[Float]]
        case value: Int => ints ++= values.asInstanceOf[WrappedArray[Int]]
        case value: Long => longs ++= values.asInstanceOf[WrappedArray[Long]]
        case value: Short => shorts ++= values.asInstanceOf[WrappedArray[Short]]

      }
    }
}

