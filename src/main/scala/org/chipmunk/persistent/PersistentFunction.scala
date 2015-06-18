package org.chipmunk.persistent

import org.squeryl.annotations.Transient

abstract class PersistentFunction[T <: PersistentFunction[T, F], F](val pClass: PersistentClass)
    extends PersistentEntity[T] {
  self: T =>

  def this(fClass: Class[_ <: F]) = this(new PersistentClass(fClass))

  @Transient // java annotation
  @transient
  private lazy val _f: F = instanceF()

  // no, this isn't a Java-ism, it's just that 'loadF' wasn't declarative enough
  def getF(): F = _f

  private def instanceF(): F = {
    val clazz = pClass.asClass
    val constructor = clazz.getConstructor()
    val innerCondAsObject = constructor.newInstance()

    innerCondAsObject.asInstanceOf[F]
  }
}