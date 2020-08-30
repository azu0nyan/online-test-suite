package controller.db.codecs

import org.bson.{BsonInvalidOperationException, BsonReader, BsonWriter}
import org.bson.codecs.{Codec, DecoderContext, EncoderContext}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import otsbridge.DisplayMe

object DisplayMeCodecProvider extends CodecProvider {
  def isCaseObjectEnum[T](clazz: Class[T]): Boolean = {
    clazz.isInstance(DisplayMe.OwnPage) || clazz.isInstance(DisplayMe.Inline)
  }

  override def get[T](clazz: Class[T], registry: CodecRegistry): Codec[T] =
    if (isCaseObjectEnum(clazz)) {
      DisplayMeCodec.asInstanceOf[Codec[T]]
    } else {
      null
    }

  object DisplayMeCodec extends Codec[DisplayMe] {
    val identifier = "_t"
    override def decode(reader: BsonReader, decoderContext: DecoderContext): DisplayMe = {
      reader.readStartDocument()
      val enumName = reader.readString(identifier)
      reader.readEndDocument()
      enumName match {
        case "OwnPage" => DisplayMe.OwnPage
        case "Inline" => DisplayMe.Inline
        case _ => throw new BsonInvalidOperationException(s"$enumName is an invalid value for a CaseObjectEnum object")
      }
    }

    override def encode(writer: BsonWriter, value: DisplayMe, encoderContext: EncoderContext): Unit = {
      val name = value match {
        case DisplayMe.OwnPage => "OwnPage"
        case DisplayMe.Inline => "Inline"
      }
      writer.writeStartDocument()
      writer.writeString(identifier, name)
      writer.writeEndDocument()
    }
    override def getEncoderClass: Class[DisplayMe] = DisplayMe.getClass.asInstanceOf[Class[DisplayMe]]
  }


}
