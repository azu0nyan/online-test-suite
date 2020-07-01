package controller

import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import java.security.spec.KeySpec

object PasswordHashingSalting {

  import java.nio.charset.StandardCharsets

  private val HEX_ARRAY = "0123456789ABCDEF".map(c => c.asInstanceOf[Byte])



  def bytesToHex(bytes: Array[Byte]): String = {
    val hexChars = new Array[Byte](bytes.length * 2)
    for (j <- bytes.indices) {
      val v = bytes(j) & 0xFF
      hexChars(j * 2) = HEX_ARRAY(v >>> 4)
      hexChars(j * 2 + 1) = HEX_ARRAY(v & 0x0F)
    }
    new String(hexChars, StandardCharsets.UTF_8)
  }

  def hexToBytes(hex:String): Array[Byte] = {
    val res = new Array[Byte](hex.length >> 1)
    for (i <- res.indices) {
      res(i) = (HEX_ARRAY.indexOf(hex(i * 2)) * 16 + HEX_ARRAY.indexOf(hex(i * 2 + 1))).toByte

    }
    res
  }


  case class HashAndSalt(hash:String, salt:String)

  def hashPasswords(password: String ):HashAndSalt = {
    val r = new SecureRandom()
    val salt = new Array[Byte](16)
    r.nextBytes(salt)

    hashWithSalt(password, salt)
  }

  def hashWithSalt(password:String, salt:Array[Byte]):HashAndSalt = {
    val spec = new PBEKeySpec(password.toCharArray, salt, 65536, 128)
    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
    val hash = factory.generateSecret(spec).getEncoded
    HashAndSalt(bytesToHex(hash), bytesToHex(salt))
  }

  def checkPassword(enteredPassword:String, hash:String, salt:String):Boolean =
    hashWithSalt(enteredPassword, hexToBytes(salt)).hash == hash

}
