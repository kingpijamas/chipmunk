package org.chipmunk

import org.squeryl.PrimitiveTypeMode.compositeKey
import org.squeryl.KeyedEntity
import org.squeryl.dsl.CompositeKey2
import org.chipmunk.persistent.Id

class BinaryAssociation(val ownerId: Id, val owneeId: Id)
    extends KeyedEntity[CompositeKey2[Id, Id]] {

  // no-arg constructor
  def this() = this(0, 0)

  def id: CompositeKey2[Id, Id] = compositeKey(ownerId, owneeId)
}