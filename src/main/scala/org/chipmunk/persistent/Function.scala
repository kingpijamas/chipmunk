package org.chipmunk.persistent

import org.squeryl.annotations.Transient

//TODO: make F have to be a function!
abstract class Function[T <: Function[T, F], F](val pType: Type)
    extends Entity[T] {
  self: T =>

  def this(fClass: Class[_ <: F]) = this(new Type(fClass))

  @Transient // java annotation
  @transient
  private lazy val _f: F = instanceF()

  // no, this isn't a Java-ism, it's just that 'loadF' wasn't declarative enough
  def getF(): F = _f

  private def instanceF(): F = {
    val clazz = pType.asClass
    val constructor = clazz.getConstructor()
    val innerCondAsObject = constructor.newInstance()

    innerCondAsObject.asInstanceOf[F]
  }
}