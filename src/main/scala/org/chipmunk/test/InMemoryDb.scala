package org.chipmunk.test

import org.chipmunk.util.Configurator
import org.chipmunk.test.InMemoryDb.Adapter
import org.chipmunk.test.InMemoryDb.DriverClass
import org.h2.Driver
import org.scalatest.BeforeAndAfterEach
import org.scalatest.Suite
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.Schema
import org.squeryl.adapters.H2Adapter

object InMemoryDb {
  private val DriverClass = classOf[org.h2.Driver]
  private val Adapter = new H2Adapter
}

trait InMemoryDb extends BeforeAndAfterEach {
  self: Suite =>

  protected def testSchema: Schema

  override def beforeEach(): Unit = {
    Configurator.initialize(DriverClass, Adapter, dbUrl())

    inTransaction { testSchema.create }
    super.beforeEach()
  }

  protected def dbUrl(dbName: String = getClass.getSimpleName): String =
    s"jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1"

  override def afterEach(): Unit = {
    try {
      super.afterEach()
    } finally {
      inTransaction { testSchema.drop }
    }
  }
}
