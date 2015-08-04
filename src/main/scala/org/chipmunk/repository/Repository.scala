package org.chipmunk.repository

import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.Entity

trait Repository[T <: Entity[_]] {
  def get(id: Id): Option[T]

  def save(elem: T): T

  def remove(elem: T): Int
}
