package org.chipmunk.persistent

import org.chipmunk.Identifiable.Id

trait Repository[T <: Entity[_]] {
  def get(id: Id): Iterable[T]

  def save(elem: T): T

  def remove(elem: T): Int
}