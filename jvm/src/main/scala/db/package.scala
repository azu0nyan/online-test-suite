import java.sql.Clob

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

package object db {
  val log: Logger = Logger(LoggerFactory.getLogger("DB"))

  implicit def stringToClob(s:String):Clob = new javax.sql.rowset.serial.SerialClob(s.toCharArray());

//  implicit def clobToString(c:Clob):String = new javax.sql.rowset.serial.SerialClob(s.toCharArray());



}
