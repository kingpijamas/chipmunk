package org.chipmunk.persistent

import org.squeryl.annotations.Transient

import scala.reflect.ClassTag
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe.TypeTag

abstract class Object[T <: Object[T, O], O](val typ: Type) extends Entity[T] {
  self: T =>

  @Transient
  private[this] var obj: O = _

  def this(objClass: Class[_ <: O]) = this(new Type(objClass))

  def this(obj: O) = this(obj.getClass)

  protected def getValue()(
    implicit classTag: ClassTag[O],
    typeTag: TypeTag[O])
  : O = {
    if (obj == null) { obj = resolveObj() }
    obj
  }

  private[this] def resolveObj()(
    implicit classTag: ClassTag[O],
    typeTag: TypeTag[O])
  : O = {
    val classNameProper = typ.classNameProper
    val moduleSymbol = currentMirror.staticModule(classNameProper)
    val moduleMirror = currentMirror.reflectModule(moduleSymbol)
    val obj = moduleMirror.instance

    if (classTag.runtimeClass.isInstance(obj)) {
      obj.asInstanceOf[O]
    } else {
      val tpe = typeTag.tpe
      throw new IllegalStateException(
        s"$classNameProper is not an instance of expected type: $tpe")
    }
  }

  protected def keys: Product = Tuple1(typ.classNameProper)
}
