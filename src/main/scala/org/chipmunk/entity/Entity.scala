package org.chipmunk.entity

import scala.collection.mutable
import org.chipmunk.entity.relation.ManyToMany
import org.chipmunk.entity.relation.ManyToOne
import org.chipmunk.entity.relation.OneToMany
import org.chipmunk.entity.relation.proxy.ManyToManyProxy
import org.chipmunk.entity.relation.proxy.ManyToOneProxy
import org.chipmunk.entity.relation.proxy.OneToManyProxy
import org.chipmunk.entity.relation.proxy.RelationProxy
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

  private[entity] val relations = mutable.Buffer[RelationProxy[_]]() //TODO: make thread safe

  protected def owner[M <: Entity[_]](
    decl: => OneToManyDeclaration[T, M]): OneToMany[M] = {
    subscribe(OneToManyProxy[M](decl.value.left(this)))
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R]): ManyToMany[R] = {
    subscribe(ManyToManyProxy[R](this, true, decl.value.left(this)))
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O]): ManyToOne[O] = {
    subscribe(ManyToOneProxy[O](decl.value.right(this)))
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    subscribe(ManyToManyProxy[L](this, false, decl.value.right(this)))
  }

  private[this] def subscribe[R <: RelationProxy[_]](rel: R): R = {
    relations += rel
    rel
  }

  private[entity] def persistBody(): Unit = { table.insertOrUpdate(this) }

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
