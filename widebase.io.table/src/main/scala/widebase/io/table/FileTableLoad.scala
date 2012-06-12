package widebase.io.table

import java.io. { File, RandomAccessFile }

import org.joda.time. { LocalDate, YearMonth, Years }

import vario.filter.StreamFilter
import vario.io.VariantReader

import widebase.db.table. { PartitionMap, Table }
import widebase.io.column. { ColumnReader, FileColumnLoader }

/** Loads tables from database.
 *
 * @param path of database
 *
 * @author myst3r10n
 */
abstract class FileTableLoad(path: String) {

  import vario.filter.StreamFilter.StreamFilter

  /** Load table from database.
    *
    * @param name of table
    * @param filter self-explanatory
    * @param segmented path of segment
    *
    * @return [[widebase.db.Table]]
   */
  def apply(
    name: String,
    filter: StreamFilter = props.filters.loader)
    (implicit segmented: File = null): Table = {

    val fileExtension =
      filter match {

        case StreamFilter.Gzip => ".gz"
        case StreamFilter.Zlib => ".zip"
        case _ => ""

      }

    var filename =
      if(segmented == null)
        path
      else
        segmented.getPath

    filename += "/" + name + fileExtension

    val channel = new RandomAccessFile(filename, "r").getChannel

    val vreader = new VariantReader(channel, filter) {

      override val charset = props.charsets.loader

      override def order = props.orders.loader

    }

    val reader = new ColumnReader(vreader)(filename)

    val table = new Table

    // Read column labels
    val labels = reader.read()

    // Read column values
    labels.foreach(label => table ++= (label, reader.read()))

    reader.close

    table

  }

  /** Load table from directory table.
    *
    * @param name of table
    * @param amount values to map, 0 map all
    * @param parted partition name
    * @param segmented path of segment
    *
    * @return [[widebase.db.Table]]
   */
  def dir(
    name: String,
    amount: Int = 0)
    (implicit parted: String = null, segmented: File = null) = {

    // New table
    val table = new Table

    // Load column labels
    val loader = new FileColumnLoader(path)
    val labels = loader.load(name, ".d")(parted, segmented)

    // Load column values
    labels.foreach(label =>
      table ++=
        label ->
        loader.load(name, label, true, amount)(parted, segmented))      

    table

  }

  /** Load table from partitioned directory table by [[org.joda.time.LocalDate]]
    *
    * @param name of table
    * @param from the [[org.joda.time.LocalDate]] from
    * @param till the [[org.joda.time.LocalDate]] till
    * @param segmented path of segment
    *
    * @return [[widebase.collection.mutable.PartitionMap]]
   */
  def dates(
    name: String,
    from: LocalDate,
    till: LocalDate)(implicit segmented: File = null) = {

    val dir =
      if(segmented == null)
        path
      else
        segmented.getPath

    var parts = new PartitionMap

    var i = from

    while(i.compareTo(till) < 0) {

      val partition = i.toString

      if((new File(dir + "/" + partition + "/" + name)).exists)
        parts += partition -> this.dir(name)(partition, segmented)

      i = i.plusDays(1)

    }

    parts

  }

  /** Load table from partitioned directory table by [[scala.Int]]
    *
    * @param name of table
    * @param from the [[scala.Int]] from
    * @param till the [[scala.Int]] till
    * @param segmented path of segment
    *
    * @return [[widebase.collection.mutable.PartitionMap]]
   */
  def ints(
    name: String,
    from: Int,
    till: Int)(implicit segmented: File = null) = {

    val dir =
      if(segmented == null)
        path
      else
        segmented.getPath

    var parts = new PartitionMap

    var i = from

    for(i <- from to till) {

      val partition = i.toString

      if((new File(dir + "/" + partition + "/" + name)).exists)
        parts += partition -> this.dir(name)(partition, segmented)

    }

    parts

  }

  /** Load table from partitioned directory table by [[org.joda.time.YearMonth]]
    *
    * @param name of table
    * @param from the [[org.joda.time.YearMonth]] from
    * @param till the [[org.joda.time.YearMonth]] till
    * @param segmented path of segment
    *
    * @return [[widebase.collection.mutable.PartitionMap]]
   */
  def months(
    name: String,
    from: YearMonth,
    till: YearMonth)(implicit segmented: File = null) = {

    val dir =
      if(segmented == null)
        path
      else
        segmented.getPath

    var parts = new PartitionMap

    var i = from

    while(i.compareTo(till) < 0) {

      val partition = i.toString

      if((new File(dir + "/" + partition + "/" + name)).exists)
        parts += partition -> this.dir(name)(partition, segmented)

      i = i.plusMonths(1)

    }

    parts

  }

  /** Load table from partitioned directory table by [[org.joda.time.Years]]
    *
    * @param name of table
    * @param from the [[org.joda.time.Years]] from
    * @param till the [[org.joda.time.Years]] till
    * @param segmented path of segment
    *
    * @return [[widebase.collection.mutable.PartitionMap]]
   */
  def years(
    name: String,
    from: Years,
    till: Years)(implicit segmented: File = null) = {

    val dir =
      if(segmented == null)
        path
      else
        segmented.getPath

    var parts = new PartitionMap

    var i = from

    while(i.compareTo(till) < 0) {

      val partition = i.toString

      if((new File(dir + "/" + partition + "/" + name)).exists)
        parts += partition -> this.dir(name)(partition, segmented)

      i = i.plus(1)

    }

    parts

  }
}

