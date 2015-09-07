package org.chipmunk.schema

trait ForeignKey[T] {
  def value: T
  def set(x: T): Unit
  def isOptional: Boolean = value.isInstanceOf[Option[_]]
}
