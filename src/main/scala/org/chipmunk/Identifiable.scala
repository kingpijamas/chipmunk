package org.chipmunk

import org.chipmunk.Identifiable.Id
import org.squeryl.KeyedEntity

object Identifiable {
  type Id = Long
}

trait Identifiable extends KeyedEntity[Id] {
  private[chipmunk] var _id: Id = 0

  def id: Id = _id
}