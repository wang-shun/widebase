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

// Init Toolbox
import java.awt.Color
import widebase.toolbox.core.graph2d._
import widebase.toolbox.core.graphics._
import widebase.toolbox.core.scribe._
import widebase.toolbox.core.specgraph._
import widebase.toolbox.core.timeseries._
import widebase.toolbox.core.uitools._
import widebase.toolbox.finance._

// Convenience purpose
import org.joda.time.LocalDateTime
import widebase.stream.handler.rq.RecordListener

// Joda date and time tools
import org.joda.time. { Days, Hours, Minutes, Months, Seconds, Weeks, Years }

def now = LocalDateTime.now
def today = now.toLocalDate

