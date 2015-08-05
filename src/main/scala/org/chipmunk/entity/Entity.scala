package org.chipmunk.entity

import scala.collection.mutable
import org.chipmunk.SplittableSchema.ManyToManyDeclaration
import org.chipmunk.SplittableSchema.ManyToOneDeclaration
import org.chipmunk.SplittableSchema.OneToManyDeclaration
import org.chipmunk.relation.ManyToMany
import org.chipmunk.relation.ManyToOne
import org.chipmunk.relation.OneToMany
import org.chipmunk.relation.RelationProxy
import org.chipmunk.relation.persistent.ManyToManyImpl
import org.chipmunk.relation.persistent.ManyToOneImpl
import org.chipmunk.relation.persistent.OneToManyImpl
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Table
import scala.annotation.meta.field

abstract class Entity[T <: Entity[T]](
  @(transient @field) private[chipmunk] val table: Table[T])
    extends Identifiable with Keyed {
  self: T =>

  private[entity] val relations = mutable.Buffer[RelationProxy[_]]() //TODO: make thread safe

  protected def owner[M <: Entity[_]](
    decl: => OneToManyDeclaration[T, M]): OneToMany[M] = {
    subscribe(OneToManyImpl[M](decl.value.left(this)))
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R]): ManyToMany[R] = {
    subscribe(ManyToManyImpl[R](this, true, decl.value.left(this)))
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O]): ManyToOne[O] = {
    subscribe(ManyToOneImpl[O](decl.value.right(this)))
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    subscribe(ManyToManyImpl[L](this, false, decl.value.right(this)))
  }

  private[this] def subscribe[R <: RelationProxy[_]](rel: R): R = {
    relations += rel
    rel
  }

  private[chipmunk] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[entity] def persistRelations(): Unit = {
    relations transform { _.persist() }
  }

  private[chipmunk] def persist(): Unit = {
    inTransaction {
      //FIXME: here's where the cascading save should be put
      persistBody()
      persistRelations()
    }
  }
}
