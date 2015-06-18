package org.chipmunk.persistent

import java.sql.{ Date => JDate }

import scala.language.implicitConversions

import org.joda.time.DateTime

object Date {
  implicit def dateAsPersistentDate(date: JDate): Date = new Date(date)
}

class Date(date: JDate) {
  def asJoda: DateTime = new DateTime(date.getTime)
}