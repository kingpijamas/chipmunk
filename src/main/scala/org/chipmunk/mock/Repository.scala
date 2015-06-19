package org.chipmunk.mock

import java.sql.SQLException

import scala.collection.mutable

import org.chipmunk.persistent

object Repository {
  def apply[M <: Entity[_]](mockEntities: M*): persistent.Repository[M] = {
    new Repository[M](mutable.Set[M]() ++= mockEntities)
  }
}

private class Repository[M <: Entity[_]](elems: mutable.Set[M])
    extends persistent.Repository[M] {

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