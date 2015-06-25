package org.chipmunk.persistent.function

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror

import org.chipmunk.persistent.Type

abstract class Function[T <: Function[T, I, O], I, O](val typ: Type)
    extends FunctionLike[T, I, O] {
  self: T =>

  def this(fClass: Class[_ <: I => O]) = this(new Type(fClass))

  def this(f: I => O) = this(f.getClass)

  override protected def resolveF()(implicit classTag: ClassTag[I => O]): I => O = {
    val classNameProper = typ.classNameProper
    val moduleSymbol = currentMirror staticModule classNameProper
    val moduleMirror = currentMirror reflectModule moduleSymbol
    val obj = moduleMirror.instance

    if (classTag.runtimeClass.isInstance(obj)) {
      obj.asInstanceOf[I => O]
    } else {
      throw new IllegalStateException(
        s"$classNameProper is not an object instance of expected function type")
    }
  }

  override def equals(other: Any): Boolean = other match {
    case that: Function[T, I, O] => that.canEqual(this) &&
      typ.className == that.typ.className
    case _ => false
  }

  override def hashCode: Int = 41 * typ.className.hashCode
}