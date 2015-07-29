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

trait RelationProxy[O <: Entity[_], SRel <: Query[O]] {
  self: Relation[O, SRel] =>

  def isDirty: Boolean

  def persist(): RelationProxy[O, SRel]
}

protected[relation] trait TransientLike[O <: Entity[_], SRel <: Query[O]]
    extends RelationProxy[O, SRel] {
  self: Relation[O, SRel] =>

  def isDirty: Boolean = !cacheRel.isEmpty

  protected[this] def cacheRel: SRel
}

protected[relation] trait PersistentLike[O <: Entity[_], SRel <: Query[O]]
    extends RelationProxy[O, SRel] {
  self: Relation[O, SRel] =>

  def isDirty: Boolean = false

  def persist(): PersistentLike[O, SRel] = this
}
