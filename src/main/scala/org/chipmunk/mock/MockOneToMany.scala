package org.chipmunk.mock

import scala.annotation.implicitNotFound
import scala.collection.mutable

import org.squeryl.KeyedEntity
import org.squeryl.dsl.OneToMany

class MockOneToMany[M](val values: mutable.Set[M] = mutable.Set[M]())
    extends MockQuery[M] with OneToMany[M] {

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