package org.chipmunk.persistent.relation.impl

import org.chipmunk.persistent.relation.impl.OneToManyImpl.wrapSquerylO2M
import org.squeryl.KeyedEntity
import org.squeryl.dsl.{ OneToMany => SOneToMany }
import org.chipmunk.persistent.Entity
import org.chipmunk.persistent.relation.OneToMany
import org.chipmunk.persistent.relation.RelationSurrogate
import org.chipmunk.mock

object OneToManySurrogate {
  def apply[O <: Entity[_], M <: Entity[_]](
    owner: O,
    actualRel: SOneToMany[M])
  : OneToMany[M] =
    OneToManySurrogate[O, M](owner, actualRel, mock.OneToMany[M]())

  def apply[O <: Entity[_], M <: Entity[_]](
    owner: O,
    actualRel: SOneToMany[M],
    cacheRel: SOneToMany[M])
  : OneToMany[M] =
    new OneToManySurrogate[O, M](owner, actualRel, cacheRel)
}

class OneToManySurrogate[O <: Entity[_], M <: Entity[_]] private (
  owner: O,
  actualRel: SOneToMany[M],
  cacheRel: SOneToMany[M])
    extends RelationSurrogate[M, OneToMany[M]](actualRel, cacheRel)
    with OneToMany[M] {

  def assign(m: M): M = {
    dirty = true //TODO: check!
    relInUse.assign(m)
  }

  def associate(m: M)(implicit ev: M <:< KeyedEntity[_]): M = {
    dirty = !owner.isPersisted
    relInUse.associate(m)(ev)
  }

  def deleteAll(): Int = {
    val removeCount = relInUse.deleteAll
    dirty = false
    removeCount
  }

  def persist(): Unit = {
    if (isDirty) {
      relInUse foreach { other =>
        if (!other.isPersisted) {
          other.persistBody()
        }
        actualRel.associate(other)
      }
    }

    relInUse = actualRel
  }
}
