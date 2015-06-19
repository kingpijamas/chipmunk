package org.chipmunk.persistent

import org.squeryl.annotations.Transient

abstract class Function[T <: Function[T, I, O], I, O](val typ: Type)
    extends Entity[T] with (I => O) {
  self: T =>

  @Transient // java annotation
  @transient
  private[this] lazy val _f: (I => O) = instanceF()

  def this(fClass: Class[_ <: (I => O)]) = this(new Type(fClass))

  def apply(params: I): O = _f(params)

  private[this] def instanceF(): (I => O) = {
    val clazz = typ.asClass
    val constructor = clazz.getConstructor()
    val innerCond = constructor.newInstance()

    innerCond.asInstanceOf[(I => O)]
  }
}