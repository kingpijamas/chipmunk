package org.chipmunk.mock

import org.chipmunk.Identifiable
import org.squeryl.dsl.{ ManyToOne => SManyToOne }

class ManyToOne[O <: Identifiable](
  var value: Option[O] = None)
    extends Query[O]
    with SManyToOne[O] {

  def iterable: Iterable[O] = value

  def assign(one: O): O = {
    this.value = Some(one)
    one
  }

  def delete: Boolean = ???
}