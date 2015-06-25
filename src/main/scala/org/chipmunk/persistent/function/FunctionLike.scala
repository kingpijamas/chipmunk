package org.chipmunk.persistent.function

import scala.reflect.ClassTag

import org.chipmunk.persistent.Entity
import org.squeryl.annotations.Transient

private[function] trait FunctionLike[T <: FunctionLike[T, I, O], I, O]
    extends Entity[T] with (I => O) {
  self: T =>

  @Transient // java annotation
  @transient
  private[this] lazy val f: I => O = resolveF()

  protected def resolveF()(implicit classTag: ClassTag[I => O]): I => O

  def apply(params: I): O = f(params)
}