package widebase.stream.handler.cq

import java.io. { PrintWriter, StringWriter }

import net.liftweb.common.Logger

import org.jboss.netty.channel. {

  ChannelHandlerContext,
  ExceptionEvent,
  MessageEvent,
  SimpleChannelUpstreamHandler

}

import org.jboss.netty.handler.timeout. {

  ReadTimeoutException,
  WriteTimeoutException

}

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

import widebase.db.table.Table

import widebase.stream.codec.cq. {

  QueryMessage,
  RejectMessage,
  TableMessage,
  TableNotFoundMessage

}

import widebase.stream.handler.AuthHandler

/** Handles query.
 *
 * @param cache of database
 * @param readLock of cache
 * @param writeLock of cache
 *
 * @author myst3r10n
 */
class QueryHandler(
  cache: TableMap,
  readLock: CountLock,
  writeLock: BlockLock)
  extends SimpleChannelUpstreamHandler
  with Logger {

  override def exceptionCaught(ctx: ChannelHandlerContext, evt: ExceptionEvent) {

    evt.getCause match {

      case _: ReadTimeoutException =>
        error("Read timeout")
        evt.getChannel.close

      case _: WriteTimeoutException =>
        error("Write timeout")
        evt.getChannel.close

      case _ =>
        val message = new StringWriter
        val printer = new PrintWriter(message)
        evt.getCause.printStackTrace(printer)
        error(message.toString)
        evt.getChannel.close

    }
  }

  override def messageReceived(ctx: ChannelHandlerContext, evt: MessageEvent) {

    evt.getMessage.asInstanceOf[QueryMessage].statement match {

      case statement: String if statement.startsWith("head") =>
        val table = statement.drop(4).filterNot(c => c == ' ')

        try {

          writeLock.acquire
          readLock.waitOfUnlock

          if(cache.contains(table)) {

            evt.getChannel.write(new TableNotFoundMessage)
            return

          }
        } finally {

          writeLock.release

        }

        evt.getChannel.write(new TableMessage(head(table)))

        debug("Query done: " + statement + " by " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

      case statement: String if statement.startsWith("last") =>
        val table = statement.drop(4).filterNot(c => c == ' ')

        try {

          writeLock.acquire
          readLock.waitOfUnlock

          if(!cache.contains(table)) {

            evt.getChannel.write(new TableNotFoundMessage)
            return

          }

        } finally {

          writeLock.release

        }

        evt.getChannel.write(new TableMessage(last(table)))

        debug("Query done: " + statement + " by " +
          AuthHandler.username.get(evt.getChannel) + " @ " +
          evt.getRemoteAddress)

      case statement =>
        error("Unfamiliar with query: " + statement)
        evt.getChannel.write(new RejectMessage("QueryException"))

    }
  }

  protected def head(name: String) = {

    val table = new Table

    try {

      writeLock.acquire
      readLock.waitOfUnlock

      cache(name).foreach { case (label, column) =>

        column match {

          case column: BoolColumn => table ++= label -> BoolColumn(column.head)
          case column: ByteColumn => table ++= label -> ByteColumn(column.head)
          case column: CharColumn => table ++= label -> CharColumn(column.head)
          case column: DoubleColumn => table ++= label -> DoubleColumn(column.head)
          case column: FloatColumn => table ++= label -> FloatColumn(column.head)
          case column: IntColumn => table ++= label -> IntColumn(column.head)
          case column: LongColumn => table ++= label -> LongColumn(column.head)
          case column: ShortColumn => table ++= label -> ShortColumn(column.head)
          case column: MonthColumn => table ++= label -> MonthColumn(column.head)
          case column: DateColumn => table ++= label -> DateColumn(column.head)
          case column: MinuteColumn => table ++= label -> MinuteColumn(column.head)
          case column: SecondColumn => table ++= label -> SecondColumn(column.head)
          case column: TimeColumn => table ++= label -> TimeColumn(column.head)
          case column: DateTimeColumn => table ++= label -> DateTimeColumn(column.head)
          case column: TimestampColumn => table ++= label -> TimestampColumn(column.head)
          case column: SymbolColumn => table ++= label -> SymbolColumn(column.head)
          case column: StringColumn => table ++= label -> StringColumn(column.head)

        }
      }
    } finally {

      writeLock.release

    }

    table

  }

  protected def last(name: String) = {

    val table = new Table

    try {

      writeLock.acquire
      readLock.waitOfUnlock

      cache(name).foreach { case (label, column) =>

        column match {

          case column: BoolColumn => table ++= label -> BoolColumn(column.last)
          case column: ByteColumn => table ++= label -> ByteColumn(column.last)
          case column: CharColumn => table ++= label -> CharColumn(column.last)
          case column: DoubleColumn => table ++= label -> DoubleColumn(column.last)
          case column: FloatColumn => table ++= label -> FloatColumn(column.last)
          case column: IntColumn => table ++= label -> IntColumn(column.last)
          case column: LongColumn => table ++= label -> LongColumn(column.last)
          case column: ShortColumn => table ++= label -> ShortColumn(column.last)
          case column: MonthColumn => table ++= label -> MonthColumn(column.last)
          case column: DateColumn => table ++= label -> DateColumn(column.last)
          case column: MinuteColumn => table ++= label -> MinuteColumn(column.last)
          case column: SecondColumn => table ++= label -> SecondColumn(column.last)
          case column: TimeColumn => table ++= label -> TimeColumn(column.last)
          case column: DateTimeColumn => table ++= label -> DateTimeColumn(column.last)
          case column: TimestampColumn => table ++= label -> TimestampColumn(column.last)
          case column: SymbolColumn => table ++= label -> SymbolColumn(column.last)
          case column: StringColumn => table ++= label -> StringColumn(column.last)

        }
      }
    } finally {

      writeLock.release

    }

    table

  }
}

