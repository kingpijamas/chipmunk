package org.chipmunk.persistent.relation.impl

import org.chipmunk.persistent.relation.impl.ManyToOneImpl.wrapSquerylM2O
import org.squeryl.dsl.{ ManyToOne => SManyToOne }
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.ManyToOne
import org.chipmunk.persistent.relation.Relation
import org.chipmunk.mock
import org.chipmunk.persistent.relation.RelationSurrogate

object ManyToOneSurrogate {
  def apply[M <: Entity[_], O <: Entity[_]](
    owner: M,
    actualRel: SManyToOne[O])
  : ManyToOne[O] =
    ManyToOneSurrogate[M, O](owner, actualRel, mock.ManyToOne[O]())

  def apply[M <: Entity[_], O <: Entity[_]](
    owner: M,
    actualRel: SManyToOne[O],
    cacheRel: SManyToOne[O])
  : ManyToOne[O] =
    new ManyToOneSurrogate[M, O](owner, actualRel, cacheRel)
}

class ManyToOneSurrogate[M <: Entity[_], O <: Entity[_]] private (
  owner: M,
  actualRel: SManyToOne[O],
//  o2m: SOneToMany[M],
  cacheRel: SManyToOne[O])
    extends RelationSurrogate[O, ManyToOne[O]](actualRel, cacheRel)
    with ManyToOne[O] {

  def assign(one: O): O = {
    dirty = true //TODO: check!
    relInUse.assign(one)
  }

  def delete(): Boolean = {
    val removed = relInUse.delete
    dirty = false
    removed
  }

  // do nothing, as per squeryl's ManyToOne limitations,
  // the one responsible for persisting ManyToOnes is the OneToMany
  def persist(): Unit = { }
//  // ideally this should be done directly,
//  // but squeryl's ManyToOne limits this with its signature
//  def persist(): Unit = {
//    if (isDirty) {
//      relInUse foreach { other =>
//        if (!other.isPersisted) {
//          other.persistBody()
//        }
//        o2m.associate(owner)
//      }
//    }
//
//    relInUse = actualRel
//  }
}
