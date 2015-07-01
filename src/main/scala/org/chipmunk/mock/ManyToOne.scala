package org.chipmunk.mock

import org.chipmunk.Identifiable
import org.squeryl.dsl.{ ManyToOne => SManyToOne }

object ManyToOne {
  def apply[O <: Identifiable](value: Option[O] = None): SManyToOne[O] =
    new ManyToOne[O](value)
}

private class ManyToOne[O <: Identifiable](var value: Option[O])
    extends Query[O] with SManyToOne[O] {

  def iterable: Iterable[O] = value

  def assign(one: O): O = {
    this.value = Some(one)
    one
  }

  def delete: Boolean = {
    val hadContents = !value.isEmpty
    this.value = None
    hadContents
  }
}
