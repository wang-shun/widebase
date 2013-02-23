package widebase.testkit

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDate

import widebase.db.table.Table
import widebase.stream.handler.AuthMap

/* A cache test.
 *
 * @author myst3r10n
 */
object Cache extends Logger with Loggable {

  import widebase.stream.socket.cq

  // Init DSL
  import widebase.dsl.conversion._
  import widebase.dsl.datatype._
  import widebase.dsl.function._

  def main(args: Array[String]) {

    for(i <- 1 to 10)
      try {

        cache

      } catch {

        case e =>
          e.printStackTrace
          sys.exit(1)

      }

  }

  def cache {

    // Payload
    val commodity = Table(string("date", "close", "symbol"))

    commodity += ("2012-05-14".D, 1560.6f, "GCK2")
    commodity += ("2012-05-14".D, 28.319f, "SIK2")

    // Authorization map
    val auths = new AuthMap {

      jaas = "widebase-server"
      this += "FindMessage" -> Array("admins", "clients")
      this += "LoadMessage" -> Array("admins", "clients")
      this += "SaveMessage" -> Array("admins", "clients")

    }

    // Client/Server
    val client = cq.client
    val server = cq.server(auths)

    try {

      server.bind

      info("Listen on " + server.port)

      client.login("client", "password")//.save("commodity", commodity)
/*
      if(client.find("commodity")) {

        val loaded = client.load("commodity")

        assert(loaded("date").head == new LocalDate("2012-05-14"), error("Value unexpected: " + loaded("date").head))
        assert(loaded("close").head == 1560.6f, error("Value unexpected: " + loaded("close").head))
        assert(loaded("symbol").head == "GCK2", error("Value unexpected: " + loaded("symbol").head))

        assert(loaded("date").last == new LocalDate("2012-05-14"), error("Value unexpected: " + loaded("date").last))
        assert(loaded("close").last == 28.319f, error("Value unexpected: " + loaded("close").last))
        assert(loaded("symbol").last == "SIK2", error("Value unexpected: " + loaded("symbol").last))

      }
*/
    } catch {

      case e => throw e

    } finally {

      client.close
      server.close

    }
  }
}

