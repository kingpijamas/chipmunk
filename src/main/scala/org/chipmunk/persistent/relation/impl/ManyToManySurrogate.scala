package org.chipmunk.persistent.relation.impl

import org.chipmunk.mock
import org.chipmunk.persistent.relation.impl.ManyToManyImpl.wrapSquerylM2M
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.chipmunk.persistent.relation.Association2
import org.chipmunk.persistent.relation.ManyToMany
import org.chipmunk.persistent.relation.RelationSurrogate
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.impl.ManyToManySurrogate.A

object ManyToManySurrogate {
  private type A = Association2

  def apply[O <: Entity[_]](
    owner: Entity[_],
    owningSide: Boolean,
    actualRel: SManyToMany[O, A])
  : ManyToMany[O] = {
    val cacheRel = mock.ManyToMany[O](owner.id, owningSide)
    ManyToManySurrogate(owner, actualRel, cacheRel)
  }

  def apply[O <: Entity[_]](
    owner: Entity[_],
    actualRel: SManyToMany[O, A],
    cacheRel: SManyToMany[O, A])
  : ManyToMany[O] =
    new ManyToManySurrogate[O](owner, actualRel, cacheRel)
}

class ManyToManySurrogate[O <: Entity[_]] private (
  owner: Entity[_],
  actualRel: SManyToMany[O, A],
  cacheRel: SManyToMany[O, A])
    extends RelationSurrogate[O, ManyToMany[O]](actualRel, cacheRel)
    with ManyToMany[O] {

  //  def assign(o: O): A = { //TODO: check!
  //    dirty = true //TODO: check!
  //    relInUse.assign(o)
  //  }
  //
  //  def assign(o: O, a: A): A = { //TODO: check!
  //    dirty = true //TODO: check!
  //    relInUse.assign(o, a)
  //  }
  //
  //  def associate(o: O): A = {
  //    dirty = !owner.isPersisted
  //    relInUse.associate(o)
  //  }
  //
  //  def associate(o: O, a: A): A = {
  //    dirty = !owner.isPersisted
  //    relInUse.associate(o, a)
  //  }
  //
  //  def dissociate(o: O): Boolean = {
  //    val removed = relInUse.dissociate(o)
  //    dirty = false
  //    removed
  //  }
  //
  //  def dissociateAll(): Int = {
  //    val removeCount = relInUse.dissociateAll
  //    dirty = false
  //    removeCount
  //  }

  def persist(): Unit = {
    if (dirty) {
      cacheRel.associationMap foreach {
        case (other, assoc) =>
          if (!other.isPersisted) {
            other.persistBody()
          }
          actualRel.associate(other, assoc)
      }
    }

    relInUse = actualRel
  }

  def query: SManyToMany[O, A] = actualRel
}
