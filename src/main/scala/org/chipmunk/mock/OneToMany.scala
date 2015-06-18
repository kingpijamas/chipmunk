package org.chipmunk.mock

import scala.annotation.implicitNotFound
import scala.collection.mutable

import org.squeryl.KeyedEntity
import org.squeryl.dsl.{ OneToMany => SOneToMany }

class OneToMany[M](val values: mutable.Set[M] = mutable.Set[M]())
    extends Query[M]
    with SOneToMany[M] {

  def iterable: Iterable[M] = values

  def assign(m: M): M = {
    values += m
    m
  }

  def associate(m: M)(implicit ev: <:<[M, KeyedEntity[_]]): M = { assign(m) }

  def deleteAll(): Int = {
    val deletionCount = values.size
    values.clear()
    deletionCount
  }
}