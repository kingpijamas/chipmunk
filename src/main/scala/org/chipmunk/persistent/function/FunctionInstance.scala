package org.chipmunk.persistent.function

import scala.reflect.ClassTag

import org.chipmunk.persistent.Type

abstract class FunctionInstance[T <: FunctionInstance[T, I, O], I, O](val typ: Type)
    extends FunctionLike[T, I, O] {
  self: T =>

  def this(fClass: Class[_ <: I => O]) = this(new Type(fClass))

  def this(f: I => O) = this(f.getClass)

  override protected def resolveF()(implicit classTag: ClassTag[I => O]): I => O = {
    val clazz = typ.asClass
    val constructor = clazz.getConstructor()
    val newF = constructor.newInstance()

    if (classTag.runtimeClass.isInstance(newF)) {
      newF.asInstanceOf[I => O]
    } else {
      val classNameProper = typ.classNameProper
      throw new IllegalStateException(
        s"$classNameProper is not a subclass of expected function type")
    }
  }

  override def equals(other: Any): Boolean = other match {
    case that: FunctionInstance[T, I, O] => that.canEqual(this) &&
      typ.className == that.typ.className
    case _ => false
  }

  override def hashCode: Int = 41 * typ.className.hashCode
}