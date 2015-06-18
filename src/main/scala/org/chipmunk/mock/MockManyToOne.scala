package org.chipmunk.mock

import org.squeryl.KeyedEntity
import org.squeryl.dsl.ManyToOne

class MockManyToOne[O <: KeyedEntity[_]](var value: Option[O] = None)
    extends MockQuery[O] with ManyToOne[O] {

  def iterable: Iterable[O] = value

  def assign(one: O): O = {
    this.value = Some(one)
    one
  }

  def delete: Boolean = ???
}