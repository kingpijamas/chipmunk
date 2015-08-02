package org.chipmunk.persistent

import org.chipmunk.Keyed
import org.squeryl.Table
import org.chipmunk.Identifiable
import org.chipmunk.persistent.relation.ManyToOne
import org.chipmunk.persistent.relation.OneToMany
import org.chipmunk.persistent.relation.ManyToMany
import org.chipmunk.persistent.relation.RelationProxy
import org.chipmunk.persistent.relation.impl.ManyToManyImpl
import org.chipmunk.persistent.relation.impl.ManyToOneImpl
import scala.collection.mutable
import org.chipmunk.persistent.relation.impl.OneToManyImpl
import org.chipmunk.SplittableSchema.OneToManyDeclaration
import org.chipmunk.SplittableSchema.ManyToOneDeclaration
import org.chipmunk.SplittableSchema.ManyToManyDeclaration

abstract class Entity[T <: Entity[T]](table: Table[T])
    extends Identifiable with Keyed {
  self: T =>

  private[persistent] val relations = mutable.Buffer[RelationProxy[_]]()

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
    val rel = decl.value
    subscribe(ManyToOneImpl[O](rel.right(this)))
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    subscribe(ManyToManyImpl[L](this, false, decl.value.right(this)))
  }

  private[this] def subscribe[R <: RelationProxy[_]](rel: R): R = {
    relations += rel
    rel
  }

  private[persistent] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[persistent] def persistRelations(): Unit = {
    relations transform { _.persist() }
  }

  private[persistent] def persist(): Unit = {
    //FIXME: here's where the cascading save should be put
    persistBody()
    persistRelations()
  }
}
