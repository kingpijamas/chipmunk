package org.chipmunk.test

import org.chipmunk.schema.Schema
import org.chipmunk.test.InMemoryDb.Adapter
import org.chipmunk.test.InMemoryDb.DriverClass
import org.chipmunk.util.Configurator
import org.h2.Driver
import org.scalatest.Suite
import org.squeryl.PrimitiveTypeMode.inTransaction
import org.squeryl.adapters.H2Adapter

object InMemoryDb {
  private val DriverClass = classOf[org.h2.Driver]
  private val Adapter = new H2Adapter
}

trait InMemoryDb {
  self: Suite =>

  protected def testSchema: Schema

  protected def withTransaction(test: => Unit): Unit = {
    withDb {
      inTransaction { test }
    }
  }

  protected def withDb(code: => Unit): Unit = {
    init()
    try { code }
    finally { cleanUp() }
  }

  private[chipmunk] def init(): Unit = {
    Configurator.initialize(DriverClass, Adapter, dbUrl())

    inTransaction { testSchema.create }
  }

  protected def dbUrl(baseDbName: String = getClass.getSimpleName): String = {
    val currThreadId = Thread.currentThread.getId
    s"jdbc:h2:mem:$baseDbName$currThreadId;DB_CLOSE_DELAY=-1"
  }

  private[chipmunk] def cleanUp(): Unit = {
    inTransaction { testSchema.drop }
  }
}
