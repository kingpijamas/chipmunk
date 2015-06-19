package org.chipmunk.persistent

import org.squeryl.annotations.Transient

//TODO: make F have to be a function!
abstract class Function[T <: Function[T, F], F](val pType: Type)
    extends Entity[T] {
  self: T =>

  def this(fClass: Class[_ <: F]) = this(new Type(fClass))

  @Transient // java annotation
  @transient
  protected lazy val _f: F = instanceF()

  private[this] def instanceF(): F = {
    val clazz = pType.asClass
    val constructor = clazz.getConstructor()
    val innerCond = constructor.newInstance()

    innerCond.asInstanceOf[F]
  }
}