package io.altoo.akka.serialization.kryo.serializer.scala

import com.esotericsoftware.kryo.{Kryo, Serializer}
import com.esotericsoftware.kryo.io.{Input, Output}

import scala.runtime.EnumValue

class ScalaEnumSerializer[T <: EnumValue] extends Serializer[T]  {

  def read(kryo: Kryo, input: Input, typ: Class[_ <: T]): T = {
    val clazz = kryo.readClass(input).getType
    val name = input.readString()
    // using value instead of ordinal to make serialization more stable, e.g. allowing reordering without breaking compatibility
    clazz.getDeclaredMethod("valueOf", classOf[String]).invoke(null, name).asInstanceOf[T]
  }

  def write(kryo: Kryo, output: Output, obj: T): Unit = {
    val enumClass = obj.getClass.getSuperclass
    val name = obj.getClass.getDeclaredMethod("productPrefix").invoke(obj).asInstanceOf[String]
    kryo.writeClass(output, enumClass)
    output.writeString(name)
  }
}
