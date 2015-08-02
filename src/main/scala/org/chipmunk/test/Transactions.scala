package org.chipmunk.test

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.Suite
import org.scalatest.fixture

trait Transactions {
  self: fixture.FlatSpec =>

  def withTransaction(test: => Unit): Unit = inTransaction { test }

  def withTransaction(testF: FixtureParam => Any): FixtureParam => Any =
    (testParams: FixtureParam) => inTransaction { testF(testParams) }
}
