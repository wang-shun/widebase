package widebase.db.column

import java.nio.channels.FileChannel

import vario.data.Datatype
import vario.file.FileVariantMapper
import vario.filter.MapFilter

/** Implements a [[scala.Boolean]] column.
 *
 * @param mapper of file
 * @param records of mapper
 * @param channel of companion
 *
 * @author myst3r10n
 */
class SymbolColumn(
  protected val mapper: FileVariantMapper = null,
  protected val records: Int = 0,
  protected val channel: FileChannel = null)
  extends VariableColumn[Symbol](Datatype.Symbol) {

  import vario.data

  protected val sizeOf = data.sizeOf.int

  protected var symbolMapper: FileVariantMapper = null

  if(
    mapper != null &&
    channel != null) {

    if(mapper.mode != Datatype.Int)
      mapper.mode = Datatype.Int

    symbolMapper = new FileVariantMapper(channel)(MapFilter.Private) {

      override val charset = props.charsets.symbols

      override def order = props.orders.symbols

    }

    symbolMapper.close // Only file channel ;)

    if(symbolMapper.mode != typeOf)
      symbolMapper.mode = typeOf

  }

  protected def get(idx: Int) = {

    symbolMapper.position =
      if(idx == 0)
        0
      else {

        mapper.position = (idx - 1) * data.sizeOf.int
        mapper.readInt

      }

    mapper.position = idx * data.sizeOf.int
    symbolMapper.readSymbol(mapper.readInt - symbolMapper.position)

  }

  protected def read = mapper.readSymbol
  protected def write(value: Symbol) {

    mapper.write(value)

  }
}

/** Companion of [[widebase.db.column.SymbolColumn]].
 *
 * @author myst3r10n
 */
object SymbolColumn {

  /** Creates [[widebase.db.column.SymbolColumn]].
   *
   * @param values of column
   *
   * @author myst3r10n
   */
  def apply(values: Symbol*) = new SymbolColumn ++= values

}

