package org.chipmunk.entity.relation.m2m

import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.value.Defaultable.DefaultableLong
import org.chipmunk.value.Defaultable.defaultOf
import org.squeryl.KeyedEntity
import org.squeryl.PrimitiveTypeMode.compositeKey
import org.squeryl.dsl.CompositeKey2

object Association2 {
  def apply(owner: Identifiable, ownee: Identifiable): Association2 =
    new Association2(owner.id, ownee.id)
}

class Association2(val ownerId: Id, val owneeId: Id)
    extends KeyedEntity[CompositeKey2[Id, Id]] {

  def this() = this(defaultOf[Id], defaultOf[Id])

  def id: CompositeKey2[Id, Id] = compositeKey(ownerId, owneeId)
}
