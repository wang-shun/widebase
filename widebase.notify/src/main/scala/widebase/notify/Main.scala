package widebase.notify

import java.net.URI

import net.liftweb.common. { Loggable, Logger }

import org.apache.commons.cli. {

  HelpFormatter,
  Option,
  OptionBuilder,
  Options,
  ParseException,
  PosixParser

}

class Main extends xsbti.AppMain with Logger with Loggable {

	protected class Exit(val code: Int) extends xsbti.Exit

	def run(configuration: xsbti.AppConfiguration) = {

    // Needed: https://github.com/harrah/xsbt/issues/435#issuecomment-6681951
    Thread.currentThread.setContextClassLoader(getClass.getClassLoader)

    Main.main(configuration.arguments)

    new Exit(0)

	}
}

object Main extends Logger with Loggable {

  import widebase.stream.socket.rq

  def main(args: Array[String]) {

    val options = new Options {

      OptionBuilder.withArgName("pattern")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("publish filter")
      addOption(OptionBuilder.create('f'))

      addOption(new Option("h", "print this message"))

      OptionBuilder.withArgName("uri")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("uri of publish channel")
      addOption(OptionBuilder.create('u'))

    }

    val producer = rq.producer

    var table: String = null
    var event: String = null

    try {

      val line = (new PosixParser).parse(options, args)

      if(line.hasOption('f'))
        producer.filter(line.getOptionValue('f'))

      if(line.hasOption('h')) {

        (new HelpFormatter).printHelp("widebase-notify", options)
        sys.exit(0)

      }

      if(line.hasOption('u')) {

        val uri = new URI(line.getOptionValue('u'))

        producer.host = uri.getHost

        if(uri.getPort != -1)
          producer.port = uri.getPort

        if(uri.getUserInfo != null) {

          val colon = uri.getUserInfo.indexOf(':')

          if(colon == -1)
            producer.username = uri.getUserInfo
          else {

            producer.username = uri.getUserInfo.substring(0, colon)
            producer.password = uri.getUserInfo.substring(colon + 1)

          }
        }
      }

      if(line.getArgs.size > 1) {

        table = line.getArgs.head
        event = line.getArgs()(1)

      }

    } catch {

      case e: ParseException =>
        error(e.getMessage)
        sys.exit(1)

    }

    if(table == null) {

      error("Table not set")
      sys.exit(1)

    }

    if(event == null) {

      error("Event not set")
      sys.exit(1)

    }

    try {

      producer.login.notify(table, event)

    } finally {

      producer.close

    }
  }
}

