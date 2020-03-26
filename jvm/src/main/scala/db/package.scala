import java.io.Reader
import java.sql.Clob

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

package object db {
  val log: Logger = Logger(LoggerFactory.getLogger("DB"))

  implicit def stringToClob(s:String):Clob = new javax.sql.rowset.serial.SerialClob(s.toCharArray());

  implicit def clobToString(data:Clob):String = {
    import java.io.BufferedReader
    import java.io.IOException
    import java.sql.SQLException
    val sb: StringBuilder = new StringBuilder

    try {
      val reader: Reader  = data.getCharacterStream
      val br: BufferedReader = new BufferedReader(reader)
      var b: Int = 0
      while ( {
        -(1) != (b = br.read)
      }) {
        sb.append(b.toChar)
      }
      br.close()
    } catch {
      case e: SQLException =>
        log.error("SQL. Could not convert CLOB to string", e)
        return e.toString
      case e: IOException =>
        log.error("IO. Could not convert CLOB to string", e)
        return e.toString
    }

    sb.toString()
  }



}
