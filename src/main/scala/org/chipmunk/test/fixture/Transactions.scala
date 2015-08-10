package org.chipmunk.test.fixture

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.Suite
import org.scalatest.fixture

trait Transactions {
  self: fixture.FlatSpec =>

  def withTransaction(testF: FixtureParam => Any): FixtureParam => Any =
    (testParams: FixtureParam) => inTransaction { testF(testParams) }
}
