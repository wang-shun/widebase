def home = System.getProperty("widebase.home")

// Init API
import widebase.db.table.Table

// Init DSL
import widebase.dsl.conversion._
import widebase.dsl.datatype._
import widebase.dsl.function._

// Init Stream/CQ
import widebase.stream.socket.cq

// Init Stream/RQ
import widebase.stream.socket.rq

// Init Testkit
import widebase.testkit

// Init UI
import widebase.ui._

// Convenience purpose
import org.joda.time.LocalDateTime
import widebase.stream.handler.rq.RecordListener

// Joda date and time tools
import org.joda.time. { Days, Hours, Minutes, Months, Seconds, Weeks, Years }

def now = LocalDateTime.now
def today = now.toLocalDate

{

  object Plugin extends widebase.workspace.runtime.PluginLike {

    import widebase.workspace.runtime

    val label = "Widebase IDE CLI"
    val scope = "widebase.workspace.ide.cli"

  }

  Plugin.register

}
