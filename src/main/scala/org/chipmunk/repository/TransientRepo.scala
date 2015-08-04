package org.chipmunk.repository

import java.sql.SQLException
import scala.collection.mutable
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.entity.Entity

object TransientRepo {
  def apply[M <: Entity[_]](mockEntities: M*): org.chipmunk.repository.Repository[M] = {
    new TransientRepo[M](mutable.Set[M]() ++= mockEntities)
  }
}

private class TransientRepo[M <: Entity[_]](elems: mutable.Set[M])
    extends org.chipmunk.repository.Repository[M] {

  def get(id: Id): Option[M] = elems find { _.id == id }

  def remove(elem: M): Int = {
    if (!elems.remove(elem)) { throw new SQLException() }

    1 // # of elems removed
  }

  def save(elem: M): M = {
    // emulating current behaviour
    //TODO: change this only after changing the current behaviour!
    if (elems.contains(elem)) { throw new SQLException() }

    elems += elem
    elem
  }
}
