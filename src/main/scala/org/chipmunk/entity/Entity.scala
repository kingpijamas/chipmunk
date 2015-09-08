package org.chipmunk.entity

import scala.collection.mutable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.relation.m2m.ManyToMany
import org.chipmunk.entity.relation.m2o.ManyToOne
import org.chipmunk.entity.relation.o2m.OneToMany
import org.chipmunk.entity.relation.m2m.ManyToManyHandle
import org.chipmunk.entity.relation.m2o.ManyToOneHandle
import org.chipmunk.entity.relation.o2m.OneToManyHandle
import org.chipmunk.entity.relation.RelationHandle
import org.chipmunk.schema.Declaration.ManyToManyDeclaration
import org.chipmunk.schema.Declaration.ManyToOneDeclaration
import org.chipmunk.schema.OneToManyDeclaration
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Table
import scala.annotation.meta.field
import org.chipmunk.value.Defaultable

abstract class Entity[T <: Entity[T]](
  @(transient @field) private[chipmunk] val table: Table[T])
    extends Identifiable with Keyed {
  self: T =>

  private[entity] val handles = mutable.Buffer[RelationHandle[_]]()

  protected def owner[M <: Entity[M]](
    decl: => OneToManyDeclaration[T, M]): OneToMany[M] = {
    val handle = OneToManyHandle(this, decl.value.left, decl.fk)
    subscribe(handle)
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R]): ManyToMany[R] = {
    val squerylRel = decl.value.left(this)
    val handle = ManyToManyHandle(this, true, squerylRel)
    subscribe(handle)
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O]): ManyToOne[O] = {
    val squerylRel = decl.value.right(this)
    val handle = ManyToOneHandle(this, squerylRel)
    subscribe(handle)
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    val squerylRel = decl.value.right(this)
    val handle = ManyToManyHandle(this, false, squerylRel)
    subscribe(handle)
  }

  private[this] def subscribe[H <: RelationHandle[_]](relHandle: H): H = {
    handles += relHandle
    relHandle
  }

  private[chipmunk] def persist(): Unit = {
    inTransaction {
      //FIXME: here's where the cascading save should be put
      persistBody()
      persistRelations()
    }
  }

  private[entity] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[this] def persistRelations(): Unit = {
    handles foreach { _.persist() }
  }
}
