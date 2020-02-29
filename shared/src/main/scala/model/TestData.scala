package model
object TestData{
  import upickle.default.{ReadWriter, macroRW}
  implicit val rw: ReadWriter[TestData] = macroRW
  case class TestData (name:String, id:Int)
}


