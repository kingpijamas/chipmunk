package org.chipmunk.entity

import scala.collection.mutable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.relation.ManyToMany
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.handle.ManyToManyHandle
import org.chipmunk.entity.relation.handle.ManyToOneHandle
import org.chipmunk.entity.relation.handle.OneToManyHandle
import org.chipmunk.entity.relation.handle.RelationHandle
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

  protected def owner[M <: Entity[M]](decl: => OneToManyDeclaration[T, M])
  : OneToMany[M] = {
    val handle = OneToManyHandle(this, decl.value.left, decl.fk)
    subscribe(handle)
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R])
  : ManyToMany[R] = {
    val squerylRel = decl.value.left(this)
    val handle = ManyToManyHandle[R](this, true, squerylRel)
    subscribe(handle)
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O])
  : ManyToOne[O] = {
    val squerylRel = decl.value.right(this)
    val handle = ManyToOneHandle[O](this, squerylRel)
    subscribe(handle)
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T])
  : ManyToMany[L] = {
    val squerylRel = decl.value.right(this)
    val handle = ManyToManyHandle[L](this, false, squerylRel)
    subscribe(handle)
  }

  private[this] def subscribe[H <: RelationHandle[_]](relHandle: H): H = {
    handles += relHandle
    relHandle
  }

  private[entity] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[entity] def persistRelations(): Unit = {
    handles foreach { _.persist() }
  }

  private[chipmunk] def persist(): Unit = {
    inTransaction {
      //FIXME: here's where the cascading save should be put
      persistBody()
      persistRelations()
    }
  }
}
