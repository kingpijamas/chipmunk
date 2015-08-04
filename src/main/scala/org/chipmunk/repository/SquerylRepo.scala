package org.chipmunk.repository

import org.chipmunk.entity.Entity
import org.chipmunk.entity.Identifiable.Id
import org.squeryl.PrimitiveTypeMode.__thisDsl
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.where
import org.squeryl.Table

abstract class SquerylRepo[T <: Entity[_]] extends Repository[T] {
  protected def table: Table[T]

  def get(id: Id): Option[T] = {
    val elems = from(table)(s => where(s.id === id).select(s))
    elems.headOption
  }

  /**
   * Note that this method *ONLY CHECKS elem's id, IT DOES NOT CHECK THE DB*
   */
  def save(elem: T): T = {
    table.insertOrUpdate(elem)
  }

  def remove(elem: T): Int = {
    table.deleteWhere(_.id === elem.id)
  }
}