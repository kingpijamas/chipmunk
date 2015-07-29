package org.chipmunk.persistent

import org.chipmunk.Identifiable.Id
import org.chipmunk.persistent.Entity

trait Repository[T <: Entity[_]] {
  def get(id: Id): Option[T]

  def save(elem: T): T

  def remove(elem: T): Int
}
