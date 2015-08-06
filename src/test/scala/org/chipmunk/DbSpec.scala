package org.chipmunk

import org.chipmunk.test.InMemoryDb
import org.chipmunk.test.Transactions
import org.scalatest.Matchers
import org.scalatest.fixture

trait DbSpec
  extends fixture.FlatSpec
  with Matchers
  with TestSchema
  with Transactions
  with InMemoryDb
