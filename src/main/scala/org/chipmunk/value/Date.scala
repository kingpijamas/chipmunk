package org.chipmunk.value

import java.sql.{ Date => SQLDate }

import scala.language.implicitConversions

import org.joda.time.DateTime

object Date {
  implicit def sqlDateAsPersistentDate(date: SQLDate): Date = new Date(date)
}

class Date(date: SQLDate) {
  def asJoda: DateTime = new DateTime(date.getTime)
}
