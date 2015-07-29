package org.chipmunk.persistent

import org.chipmunk.Keyed
import org.squeryl.Table
import org.chipmunk.Identifiable
import org.chipmunk.persistent.relation.ManyToOne
import org.chipmunk.persistent.relation.OneToMany
import org.chipmunk.persistent.relation.ManyToMany
import org.chipmunk.persistent.relation.RelationSurrogate
import org.chipmunk.persistent.relation.impl.ManyToManySurrogate

abstract class Entity[T <: Entity[T]](table: Table[T])
    extends Identifiable with Keyed {
  self: T =>

  private[persistent] val relations = mutable.Buffer[RelationSurrogate[_, _]]()

  protected def owner[M <: Entity[_]](
    decl: => OneToManyDeclaration[T, M]): OneToMany[T, M] = {
    subscribe(OneToMany[T, M](this, decl.value.left(this)))
  }

  protected def owner[R <: Entity[_]](
    decl: => ManyToManyDeclaration[T, R]): ManyToMany[R] = {
    subscribe(ManyToManySurrogate[R](this, true, decl.value.left(this)))
  }

  protected def ownee[O <: Entity[_]](
    decl: => ManyToOneDeclaration[T, O]): ManyToOne[T, O] = {
    val rel = decl.value
    subscribe(ManyToOne[T, O](this, rel.right(this)))
  }

  protected def ownee[L <: Entity[_]](
    decl: => ManyToManyDeclaration[L, T]): ManyToMany[L] = {
    subscribe(ManyToManySurrogate[L](this, false, decl.value.right(this)))
  }

  private[this] def subscribe[R <: RelationSurrogate[_, _]](rel: R): R = {
    relations += rel
    rel
  }

  protected def relate[O <: Entity[_]](
    relation: OneToMany[T, O], other: O): Unit = {
    relation.associate(other)
  }

  protected def relate[O <: Entity[_]](
    relation: ManyToMany[O], other: O): Unit = {
    relation.associate(other)
  }

  private[persistent] def persistBody(): Unit = { table.insertOrUpdate(this) }

  private[persistent] def persistRelations(): Unit = {
    relations foreach { _.persist() }
  }
}
