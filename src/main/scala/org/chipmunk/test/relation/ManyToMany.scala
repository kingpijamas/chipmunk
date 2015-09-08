package org.chipmunk.test.relation

import scala.collection.mutable

import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.relation.m2m.Association2
import org.squeryl.dsl.{ ManyToMany => SManyToMany }

object ManyToMany {
  def apply[O <: Identifiable](
    outerId: Id,
    owningSide: Boolean,
    values: (O, Association2)*)
  : SManyToMany[O, Association2] =
    new ManyToMany[O](outerId, owningSide, mutable.Map() ++= values)
}

private class ManyToMany[O <: Identifiable](
  outerId: Id,
  owningSide: Boolean,
  values: mutable.Map[O, Association2])
    extends Query[O] with SManyToMany[O, Association2] {

  def iterable: Iterable[O] = values map { _._1 }

  def assign(o: O): Association2 = {
    val ownerId = if (owningSide) outerId else o.id
    val owneeId = if (owningSide) o.id else outerId

    val assoc = new Association2(ownerId = ownerId, owneeId = owneeId)
    assign(o, assoc)
  }

  /**
   * CHECK: Os will almost always be persistent entities (and thus should
   * have overriden hashCode). If this weren't the case, hashing *will* be a
   * problem when trying to use this class
   */
  def assign(o: O, a: Association2): Association2 = {
    values += o -> a
    a
  }

  def associate(o: O): Association2 = { assign(o) }

  def associate(o: O, a: Association2): Association2 = { assign(o, a) }

  def associationMap: Query[(O, Association2)] = new Query[(O, Association2)] {
    def iterable: Iterable[(O, Association2)] = values
  }

  def associations: Query[Association2] = new Query[Association2] {
    def iterable: Iterable[Association2] = values map { _._2 }
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
