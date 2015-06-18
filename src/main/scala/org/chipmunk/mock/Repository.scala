package org.chipmunk.mock

import java.sql.SQLException

import scala.collection.mutable

import org.chipmunk.persistent.{ Repository => RealRepository }

class Repository[M <: Entity[_]] extends RealRepository[M] {
  val elems: mutable.Set[M] = mutable.Set[M]()

  def get(id: Long): Iterable[M] = elems find { _.id == id }

  def remove(elem: M): Int = {
    if (!elems.remove(elem)) { throw new SQLException() }

    elem._isMockPersisted = false // TODO:check!
    1 // # of elems removed
  }

  def save(elem: M): M = {
    // emulating current behaviour
    //TODO: change this only after changing the current behaviour!
    if (elems.contains(elem)) { throw new SQLException() }

    elems += elem
    elem._isMockPersisted = true // TODO:check!
    elem
  }
}