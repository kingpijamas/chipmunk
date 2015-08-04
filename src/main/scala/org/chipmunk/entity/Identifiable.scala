package org.chipmunk.entity

import org.chipmunk.entity.Identifiable.Id
import org.squeryl.KeyedEntity

object Identifiable {
  type Id = Long
}

trait Identifiable extends KeyedEntity[Id] {
  /*
   * cannot just be hidden or a val, or else it will not be
   * fully usable as an index!
   */
  var id: Id = 0
}
