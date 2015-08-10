package org.chipmunk.test

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.Suite

trait Transactions {
  self: Suite =>

  def withTransaction(test: => Unit): Unit = inTransaction { test }
}
