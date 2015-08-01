package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.Query

//abstract class RelationSurrogate[O <: Entity[_], Rel <: Relation[O, _]](
//    actualRel: Rel,
//    cacheRel: Rel) {
//  self: Rel =>
//
//  @transient
//  protected var relInUse: Rel = cacheRel
//
//  @transient
//  protected var dirty: Boolean = false
//
//  def isDirty: Boolean = dirty
//
//  def add(other: O): Unit = {
//    dirty = true
//    cacheRel.add(other)
//  }
//
//  def remove(other: O): Unit = {
//    cacheRel.remove(other)
//    dirty = false
//  }
//
//  def persist(): Unit
//}

trait RelationProxy[O <: Entity[_]] {
  self: Relation[O] =>

  def isDirty: Boolean

  def persist(): RelationProxy[O]

  final def query: SRel = rel

  protected[this] def rel: SRel
}

protected[relation] trait TransientLike[O <: Entity[_]]
    extends RelationProxy[O] {
  self: Relation[O] =>

  final def isDirty: Boolean = !rel.isEmpty
}

protected[relation] trait PersistentLike[O <: Entity[_]]
    extends RelationProxy[O] {
  self: Relation[O] =>

  final def isDirty: Boolean = false

  final def persist(): this.type = this
}
