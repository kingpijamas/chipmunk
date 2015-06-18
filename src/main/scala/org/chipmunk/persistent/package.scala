package org.chipmunk

import org.squeryl.KeyedEntity

package object persistent {
  type Id = Long

  trait Identifiable extends KeyedEntity[Id] {
    var id: Id = 0
  }
}