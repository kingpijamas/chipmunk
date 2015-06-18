package org.chipmunk.mock

import scala.collection.mutable
import org.chipmunk.BinaryAssociation
import org.chipmunk.mock.ManyToMany.A
import org.squeryl.{ Query => SQuery }
import org.squeryl.dsl.{ ManyToMany => SManyToMany }
import org.chipmunk.Identifiable

object ManyToMany {
  private[ManyToMany]type A = BinaryAssociation
}

class ManyToMany[O <: Identifiable](
  val owningSide: Boolean,
  val outerIdGetter: Unit => Long,
  val values: mutable.Set[(O, A)] = mutable.Set[(O, A)]())
    extends Query[O]
    with SManyToMany[O, A] {

  def iterable: Iterable[O] = values map { _._1 }

  def assign(o: O): A = {
    val outerId = outerIdGetter(())

    val ownerId = if (owningSide) outerId else o.id
    val owneeId = if (owningSide) o.id else outerId

    val assoc = new BinaryAssociation(ownerId = ownerId, owneeId = owneeId)
    assign(o, assoc)
  }

  def assign(o: O, a: A): A = {
    //CHECK: Os will almost always be persistent entities (and thus should have overriden hashCode).
    // If this weren't the case, hashing *will* be a problem when trying to use this class
    values += o -> a
    a
  }

  def associate(o: O): A = { assign(o) }

  def associate(o: O, a: A): A = { assign(o, a) }

  def associationMap: Query[(O, A)] = new Query[(O, A)] {
    def iterable: Iterable[(O, A)] = values
  }

  def associations: Query[A] = new Query[A] {
    def iterable: Iterable[A] = values map { _._2 }
  }

  def dissociate(o: O): Boolean = ???

  def dissociateAll: Int = ???
}