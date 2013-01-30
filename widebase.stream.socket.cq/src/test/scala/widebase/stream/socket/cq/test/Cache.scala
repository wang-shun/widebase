package widebase.stream.socket.cq.test

import net.liftweb.common. { Loggable, Logger }

import org.joda.time.LocalDate

import widebase.db.column.StringColumn
import widebase.db.table.Table
import widebase.stream.handler.AuthMap

/* A cache test.
 *
 * @author myst3r10n
 */
object Cache extends Logger with Loggable {

  import widebase.stream.socket.cq

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
    val commodity = Table(StringColumn("date", "close", "symbol"))

    commodity += (new LocalDate("2012-05-14"), 1560.6f, "GCK2")
    commodity += (new LocalDate("2012-05-14"), 28.319f, "SIK2")

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

      client.login("client", "password").save("commodity", commodity)

      if(client.find("commodity")) {

        val loaded = client.load("commodity")

        println(loaded)

      }
    } catch {

      case e => throw e

    } finally {

      client.close
      server.close

    }
  }
}

