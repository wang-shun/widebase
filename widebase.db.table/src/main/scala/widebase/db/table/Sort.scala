package widebase.db.table

import java.sql.Timestamp

import org.joda.time. {

  LocalDate,
  LocalDateTime,
  LocalTime,
  Minutes,
  Seconds,
  YearMonth

}

import widebase.data.Datatype

import widebase.db.column. {

  BoolColumn,
  ByteColumn,
  CharColumn,
  DoubleColumn,
  FloatColumn,
  IntColumn,
  LongColumn,
  ShortColumn,
  MonthColumn,
  DateColumn,
  MinuteColumn,
  SecondColumn,
  TimeColumn,
  DateTimeColumn,
  TimestampColumn,
  SymbolColumn,
  StringColumn

}

/** Sort algorithms.
 *
 * @author myst3r10n
 */
object Sort {

  import SortDirection.SortDirection

  /** Sort table by insertion algorithm
    *
    * @param table to sort
    * @param label label of column
    * @param direction the sort direction
   */
  def insertion(
    table: Table,
    label: Any,
    direction: SortDirection) {

    val primary = table(label)

    for(i <- 1 to primary.length - 1) {

      var j = i - 1
      var done = false
      val value = primary(i)
      val values = (for(column <- table.columns) yield(column(i))).toBuffer

      do {

        var trigger = false

        primary.typeOf match {

          case Datatype.Bool =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Boolean]
                  .compareTo(value.asInstanceOf[Boolean]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Boolean]
                  .compareTo(value.asInstanceOf[Boolean]) < 0)
                  trigger = true

            }

          case Datatype.Byte =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Byte]
                  .compareTo(value.asInstanceOf[Byte]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Byte]
                  .compareTo(value.asInstanceOf[Byte]) < 0)
                  trigger = true

            }

          case Datatype.Char =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Char]
                  .compareTo(value.asInstanceOf[Char]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Char]
                  .compareTo(value.asInstanceOf[Char]) < 0)
                  trigger = true

            }

          case Datatype.Double =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Double]
                  .compareTo(value.asInstanceOf[Double]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Double]
                  .compareTo(value.asInstanceOf[Double]) < 0)
                  trigger = true

            }

          case Datatype.Float =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Float]
                  .compareTo(value.asInstanceOf[Float]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Float]
                  .compareTo(value.asInstanceOf[Float]) < 0)
                  trigger = true

            }

          case Datatype.Int =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Int]
                  .compareTo(value.asInstanceOf[Int]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Int]
                  .compareTo(value.asInstanceOf[Int]) < 0)
                  trigger = true

            }

          case Datatype.Long =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Long]
                  .compareTo(value.asInstanceOf[Long]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Long]
                  .compareTo(value.asInstanceOf[Long]) < 0)
                  trigger = true

            }

          case Datatype.Short =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Short]
                  .compareTo(value.asInstanceOf[Short]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Short]
                  .compareTo(value.asInstanceOf[Short]) < 0)
                  trigger = true

            }

          case Datatype.Month =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[YearMonth]
                  .compareTo(value.asInstanceOf[YearMonth]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[YearMonth]
                  .compareTo(value.asInstanceOf[YearMonth]) < 0)
                  trigger = true

            }

          case Datatype.Date =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[LocalDate]
                  .compareTo(value.asInstanceOf[LocalDate]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[LocalDate]
                  .compareTo(value.asInstanceOf[LocalDate]) < 0)
                  trigger = true

            }

          case Datatype.Minute =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Minutes]
                  .compareTo(value.asInstanceOf[Minutes]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Minutes]
                  .compareTo(value.asInstanceOf[Minutes]) < 0)
                  trigger = true

            }

          case Datatype.Second =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Seconds]
                  .compareTo(value.asInstanceOf[Seconds]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Seconds]
                  .compareTo(value.asInstanceOf[Seconds]) < 0)
                  trigger = true

            }

          case Datatype.Time =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[LocalTime]
                  .compareTo(value.asInstanceOf[LocalTime]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[LocalTime]
                  .compareTo(value.asInstanceOf[LocalTime]) < 0)
                  trigger = true

            }

          case Datatype.DateTime =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[LocalDateTime]
                  .compareTo(value.asInstanceOf[LocalDateTime]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[LocalDateTime]
                  .compareTo(value.asInstanceOf[LocalDateTime]) < 0)
                  trigger = true

            }

          case Datatype.Timestamp =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Timestamp]
                  .compareTo(value.asInstanceOf[Timestamp]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Timestamp]
                  .compareTo(value.asInstanceOf[Timestamp]) < 0)
                  trigger = true

            }

          case Datatype.Symbol =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[Symbol].toString
                  .compareTo(value.asInstanceOf[Symbol].toString) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[Symbol].toString
                  .compareTo(value.asInstanceOf[Symbol].toString) < 0)
                  trigger = true

            }

          case Datatype.String =>
            direction match {

              case SortDirection.Ascending =>
                if(primary(j).asInstanceOf[String]
                  .compareTo(value.asInstanceOf[String]) > 0)
                  trigger = true

              case SortDirection.Descending =>
                if(primary(j).asInstanceOf[String]
                  .compareTo(value.asInstanceOf[String]) < 0)
                  trigger = true

            }

        }

        if(trigger) {

          table.columns.foreach {

            case column: BoolColumn => column(j + 1) = column(j)
            case column: ByteColumn => column(j + 1) = column(j)
            case column: CharColumn => column(j + 1) = column(j)
            case column: DoubleColumn => column(j + 1) = column(j)
            case column: FloatColumn => column(j + 1) = column(j)
            case column: IntColumn => column(j + 1) = column(j)
            case column: LongColumn => column(j + 1) = column(j)
            case column: ShortColumn => column(j + 1) = column(j)
            case column: MonthColumn => column(j + 1) = column(j)
            case column: DateColumn => column(j + 1) = column(j)
            case column: MinuteColumn => column(j + 1) = column(j)
            case column: SecondColumn => column(j + 1) = column(j)
            case column: TimeColumn => column(j + 1) = column(j)
            case column: DateTimeColumn => column(j + 1) = column(j)
            case column: TimestampColumn => column(j + 1) = column(j)
            case column: SymbolColumn =>column(j + 1) = column(j)
            case column: StringColumn =>column(j + 1) = column(j)

          }

          j -= 1

          if(j < 0)
            done = true

        } else
          done = true

      } while(!done)

      var k = 0

      table.columns.foreach { column =>

        column match {

          case column: BoolColumn => column(j + 1) = values(k).asInstanceOf[Boolean]
          case column: ByteColumn => column(j + 1) = values(k).asInstanceOf[Byte]
          case column: CharColumn => column(j + 1) = values(k).asInstanceOf[Char]
          case column: DoubleColumn => column(j + 1) = values(k).asInstanceOf[Double]
          case column: FloatColumn => column(j + 1) = values(k).asInstanceOf[Float]
          case column: IntColumn => column(j + 1) = values(k).asInstanceOf[Int]
          case column: LongColumn => column(j + 1) = values(k).asInstanceOf[Long]
          case column: ShortColumn => column(j + 1) = values(k).asInstanceOf[Short]
          case column: MonthColumn => column(j + 1) = values(k).asInstanceOf[YearMonth]
          case column: DateColumn => column(j + 1) = values(k).asInstanceOf[LocalDate]
          case column: MinuteColumn => column(j + 1) = values(k).asInstanceOf[Minutes]
          case column: SecondColumn => column(j + 1) = values(k).asInstanceOf[Seconds]
          case column: TimeColumn => column(j + 1) = values(k).asInstanceOf[LocalTime]
          case column: DateTimeColumn => column(j + 1) = values(k).asInstanceOf[LocalDateTime]
          case column: TimestampColumn => column(j + 1) = values(k).asInstanceOf[Timestamp]
          case column: SymbolColumn => column(j + 1) = values(k).asInstanceOf[Symbol]
          case column: StringColumn => column(j + 1) = values(k).asInstanceOf[String]

        }

        k += 1

      }
    }
  }

  /** Sort table by selection algorithm
    *
    * @param table to sort
    * @param label label of column
    * @param direction the sort direction
   */
  def selection(
    table: Table,
    label: Any,
    direction: SortDirection) {

    val primary = table(label)

    for(i <- 0 to primary.length - 1) {

      var pos = -1
      var value: Any = null

      for(j <- i to primary.length - 1) {

        if(j == i) {

          pos = j
          value = primary(pos)

        } else {

          var trigger = false

          primary.typeOf match {

            case Datatype.Bool =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Boolean]
                    .compareTo(value.asInstanceOf[Boolean]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Boolean]
                    .compareTo(value.asInstanceOf[Boolean]) > 0)
                    trigger = true

              }

            case Datatype.Byte =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Byte]
                    .compareTo(value.asInstanceOf[Byte]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Byte]
                    .compareTo(value.asInstanceOf[Byte]) > 0)
                    trigger = true

              }

            case Datatype.Char =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Char]
                    .compareTo(value.asInstanceOf[Char]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Char]
                    .compareTo(value.asInstanceOf[Char]) > 0)
                    trigger = true

              }

            case Datatype.Double =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Double]
                    .compareTo(value.asInstanceOf[Double]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Double]
                    .compareTo(value.asInstanceOf[Double]) > 0)
                    trigger = true

              }

            case Datatype.Float =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Float]
                    .compareTo(value.asInstanceOf[Float]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Float]
                    .compareTo(value.asInstanceOf[Float]) > 0)
                    trigger = true

              }

            case Datatype.Int =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Int]
                    .compareTo(value.asInstanceOf[Int]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Int]
                    .compareTo(value.asInstanceOf[Int]) > 0)
                    trigger = true

              }

            case Datatype.Long =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Long]
                    .compareTo(value.asInstanceOf[Long]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Long]
                    .compareTo(value.asInstanceOf[Long]) > 0)
                    trigger = true

              }

            case Datatype.Short =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Short]
                    .compareTo(value.asInstanceOf[Short]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Short]
                    .compareTo(value.asInstanceOf[Short]) > 0)
                    trigger = true

              }

            case Datatype.Month =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[YearMonth]
                    .compareTo(value.asInstanceOf[YearMonth]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[YearMonth]
                    .compareTo(value.asInstanceOf[YearMonth]) > 0)
                    trigger = true

              }

            case Datatype.Date =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[LocalDate]
                    .compareTo(value.asInstanceOf[LocalDate]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[LocalDate]
                    .compareTo(value.asInstanceOf[LocalDate]) > 0)
                    trigger = true

              }

            case Datatype.Minute =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Minutes]
                    .compareTo(value.asInstanceOf[Minutes]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Minutes]
                    .compareTo(value.asInstanceOf[Minutes]) > 0)
                    trigger = true

              }

            case Datatype.Second =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Seconds]
                    .compareTo(value.asInstanceOf[Seconds]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Seconds]
                    .compareTo(value.asInstanceOf[Seconds]) > 0)
                    trigger = true

              }

            case Datatype.Time =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[LocalTime]
                    .compareTo(value.asInstanceOf[LocalTime]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[LocalTime]
                    .compareTo(value.asInstanceOf[LocalTime]) > 0)
                    trigger = true

              }

            case Datatype.DateTime =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[LocalDateTime]
                    .compareTo(value.asInstanceOf[LocalDateTime]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[LocalDateTime]
                    .compareTo(value.asInstanceOf[LocalDateTime]) > 0)
                    trigger = true

              }

            case Datatype.Timestamp =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Timestamp]
                    .compareTo(value.asInstanceOf[Timestamp]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Timestamp]
                    .compareTo(value.asInstanceOf[Timestamp]) > 0)
                    trigger = true

              }

            case Datatype.Symbol =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[Symbol].toString
                    .compareTo(value.asInstanceOf[Symbol].toString) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[Symbol].toString
                    .compareTo(value.asInstanceOf[Symbol].toString) > 0)
                    trigger = true

              }

            case Datatype.String =>
              direction match {

                case SortDirection.Ascending =>
                  if(primary(j).asInstanceOf[String]
                    .compareTo(value.asInstanceOf[String]) < 0)
                    trigger = true

                case SortDirection.Descending =>
                  if(primary(j).asInstanceOf[String]
                    .compareTo(value.asInstanceOf[String]) > 0)
                    trigger = true

              }

          }

          if(trigger) {

            pos = j
            value = primary(pos)

          }
        }
      }

      if(i != pos)
        table.columns.foreach {

          case column: BoolColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: ByteColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: CharColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: DoubleColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: FloatColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: IntColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: LongColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: ShortColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: MonthColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: DateColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: MinuteColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: SecondColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: TimeColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: DateTimeColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: TimestampColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: SymbolColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

          case column: StringColumn =>
            val backup = column(pos)
            column(pos) = column(i)
            column(i) = backup

        }
    }
  }
}
