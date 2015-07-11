package org.chipmunk.persistent

import org.chipmunk.Identifiable.Id
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode.compositeKey
import org.squeryl.dsl.CompositeKey2

class BinaryAssociation(val ownerId: Id, val owneeId: Id)
    extends KeyedEntity[CompositeKey2[Id, Id]] {

  // no-arg constructor
  def this() = this(0, 0)

  def id: CompositeKey2[Id, Id] = compositeKey(ownerId, owneeId)
}
