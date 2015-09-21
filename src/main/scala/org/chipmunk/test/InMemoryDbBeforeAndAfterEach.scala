package org.chipmunk.test

import org.scalatest.BeforeAndAfterEach
import org.scalatest.Suite
import org.squeryl.PrimitiveTypeMode.inTransaction

trait InMemoryDbBeforeAndAfterEach
    extends BeforeAndAfterEach
    with InMemoryDb {
  self: Suite =>

  override def beforeEach(): Unit = {
    init()
    super.beforeEach()
  }

  override protected def withTransaction(test: => Unit): Unit = {
    inTransaction { test }
  }

  override def afterEach(): Unit = {
    try {
      super.afterEach()
    } finally {
      inTransaction { cleanUp() }
    }
  }
}
