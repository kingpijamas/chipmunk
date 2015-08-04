package org.chipmunk.relation.mock

import scala.collection.mutable

import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.relation.Association2
import org.chipmunk.relation.mock.ManyToMany.A
import org.squeryl.dsl.{ ManyToMany => SManyToMany }

object ManyToMany {
  private type A = Association2

  def apply[O <: Identifiable](
    outerId: Id,
    owningSide: Boolean,
    values: (O, A)*)
  : SManyToMany[O, A] =
    new ManyToMany[O](outerId, owningSide, mutable.Map() ++= values)
}

private class ManyToMany[O <: Identifiable](
  outerId: Id,
  owningSide: Boolean,
  values: mutable.Map[O, A])
    extends TransientQuery[O] with SManyToMany[O, A] {

  def iterable: Iterable[O] = values map { _._1 }

  def assign(o: O): Association2 = {
    val ownerId = if (owningSide) outerId else o.id
    val owneeId = if (owningSide) o.id else outerId

    val assoc = new Association2(ownerId = ownerId, owneeId = owneeId)
    assign(o, assoc)
  }

  def assign(o: O, a: A): Association2 = {
    //CHECK: Os will almost always be persistent entities (and thus should have overriden hashCode).
    // If this weren't the case, hashing *will* be a problem when trying to use this class
    values += o -> a
    a
  }

  def associate(o: O): Association2 = { assign(o) }

  def associate(o: O, a: A): Association2 = { assign(o, a) }

  def associationMap: TransientQuery[(O, A)] = new TransientQuery[(O, A)] {
    def iterable: Iterable[(O, A)] = values
  }

  def associations: TransientQuery[A] = new TransientQuery[A] {
    def iterable: Iterable[A] = values map { _._2 }
  }

  def dissociate(o: O): Boolean = {
    val formerSize = values.size
    values -= o
    values.size < formerSize
  }

  def dissociateAll: Int = {
    val formerSize = values.size
    values.clear()
    formerSize
  }
}
