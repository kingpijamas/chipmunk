package org.chipmunk.persistent

import org.joda.time.Duration
import org.squeryl.customtypes.LongField

class PersistentDuration(millis: Long) extends LongField(millis) {
  require(millis >= 0)

  // no-arg constructor
  def this() = this(0)

  def this(duration: Duration) = this(duration.getMillis)

  def asJoda: Duration = new Duration(millis)
}