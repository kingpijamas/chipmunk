package org.chipmunk

import org.chipmunk.Identifiable.Id
import org.squeryl.KeyedEntity

object Identifiable {
  type Id = Long
}

trait Identifiable extends KeyedEntity[Id] {
  var id: Id = 0 // cannot just be hidden or a val, or else it will not be fully usable as an index!
}
