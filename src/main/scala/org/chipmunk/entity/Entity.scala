package org.chipmunk.entity

import scala.collection.mutable
import org.chipmunk.entity.relation.ManyToMany
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.handle.ManyToManyHandle
import org.chipmunk.entity.relation.handle.ManyToOneHandle
import org.chipmunk.entity.relation.handle.OneToManyHandle
import org.chipmunk.entity.relation.handle.RelationHandle
import org.chipmunk.schema.SplittableSchema.ManyToManyDeclaration
import org.chipmunk.schema.SplittableSchema.ManyToOneDeclaration
import org.chipmunk.schema.SplittableSchema.OneToManyDeclaration
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Table
import scala.annotation.meta.field

abstract class Entity[T <: Entity[T]](
  @(transient @field) private[chipmunk] val table: Table[T])
    extends Identifiable with Keyed {
  self: T =>

  private[entity] val relations = mutable.Buffer[RelationHandle[_]]() //TODO: make this thread safe

  protected def owner[M <: Entity[_]](
    decl: => OneToManyDeclaration[T, M]): OneToMany[M] = {
    val sRel = decl.value.left(this)
    subscribe(OneToManyHandle[M](transient = !isPersisted, sRel))
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R]): ManyToMany[R] = {
    val sRel = decl.value.left(this)
    subscribe(ManyToManyHandle[R](transient = !isPersisted, this, true, sRel))
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O]): ManyToOne[O] = {
    val sRel = decl.value.right(this)
    subscribe(ManyToOneHandle[O](transient = !isPersisted, sRel))
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    val sRel = decl.value.right(this)
    subscribe(ManyToManyHandle[L](transient = !isPersisted, this, false, sRel))
  }

  private[this] def subscribe[H <: RelationHandle[_]](relHandle: H): H = {
    relations += relHandle
    relHandle
  }

  private[entity] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[entity] def persistRelations(): Unit = {
    relations foreach { _.persist() }
  }

  private[chipmunk] def persist(): Unit = {
    inTransaction {
      //FIXME: here's where the cascading save should be put
      persistBody()
      persistRelations()
    }
  }
}
