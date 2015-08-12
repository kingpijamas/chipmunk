package org.chipmunk.entity.relation

import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.value.Defaultable.DefaultableLong
import org.chipmunk.value.Defaultable.defaultOf
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode.compositeKey
import org.squeryl.dsl.CompositeKey2

class Association2(val ownerId: Id, val owneeId: Id)
    extends KeyedEntity[CompositeKey2[Id, Id]] {

  def this() = this(defaultOf[Id], defaultOf[Id])

  def id: CompositeKey2[Id, Id] = compositeKey(ownerId, owneeId)
}
