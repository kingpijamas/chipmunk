package org.chipmunk

import org.chipmunk.Identifiable.Id
import org.squeryl.KeyedEntity

object Identifiable {
  type Id = Long
}

trait Identifiable extends KeyedEntity[Id] {
  var id: Id = 0
}