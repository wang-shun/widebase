package widebase.plant

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

      OptionBuilder.withArgName("file")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("path of the authorization file")
      addOption(OptionBuilder.create('a'))

      OptionBuilder.withArgName("pattern")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("publish-subscribe filter")
      addOption(OptionBuilder.create('f'))

      addOption(new Option("h", "print this message"))

      OptionBuilder.withArgName("seconds")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("publishing interval")
      addOption(OptionBuilder.create('i'))

      OptionBuilder.withArgName("number")
      OptionBuilder.hasArg
      OptionBuilder.withDescription("port to listen clients")
      addOption(OptionBuilder.create('p'))

    }

    val broker = rq.broker

    try {

      val line = (new PosixParser).parse(options, args)

      if(line.hasOption('a'))
        broker.load(line.getOptionValue('a'))

      if(line.hasOption('f'))
        broker.filter(line.getOptionValue('f'))

      if(line.hasOption('h')) {

        (new HelpFormatter).printHelp("widebase-cli", options)
        sys.exit(0)

      }

      if(line.hasOption('i'))
        broker.interval = line.getOptionValue('i').toInt

      if(line.hasOption('p'))
        broker.port = line.getOptionValue('p').toInt

      if(!line.getArgs.isEmpty)
        broker.path = line.getArgs.head

    } catch {

      case e: ParseException =>
        error(e.getMessage)
        sys.exit(1)

    }

    try {

      broker.bind

      info("Listen on " + broker.port)

      broker.await

    } finally {

      broker.close

    }
  }
}

