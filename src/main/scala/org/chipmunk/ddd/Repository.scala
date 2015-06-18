package org.chipmunk.ddd

import org.chipmunk.persistent.PersistentEntity
import org.chipmunk.persistent.Id

trait Repository[T <: PersistentEntity[_]] {
  def get(id: Id): Iterable[T]

  def save(elem: T): T

  def remove(elem: T): Int
}