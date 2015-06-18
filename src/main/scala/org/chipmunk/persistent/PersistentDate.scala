package org.chipmunk.persistent

import java.sql.Date

import scala.language.implicitConversions

import org.joda.time.DateTime

object PersistentDate {
  implicit def dateAsPersistentDate(date: Date): PersistentDate =
    new PersistentDate(date)
}

class PersistentDate(date: Date) {
  def asJoda: DateTime = new DateTime(date.getTime)
}