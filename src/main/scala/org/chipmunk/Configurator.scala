package org.chipmunk

import java.sql.DriverManager
import org.squeryl.Session
import org.squeryl.SessionFactory
import java.sql.Driver
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.PrimitiveTypeMode.inTransaction

object Configurator {
  def initialize(
    driverName: String,
    adapter: DatabaseAdapter,
    dbUrl: String)
  : Unit = {
    // initialize driver in the old JDBC fashion
    val clazz = Class.forName(driverName)
    val driverClass = clazz.asInstanceOf[Class[_ <: Driver]]

    initialize(driverClass, adapter, dbUrl)
  }

  def initialize(
    driver: Class[_ <: Driver],
    adapter: DatabaseAdapter,
    dbUrl: String)
  : Unit = {
    SessionFactory.concreteFactory = Some(() => {
      val connection = DriverManager.getConnection(dbUrl)
      Session.create(connection, adapter)
    })
  }

  def logSql(logger: String => Unit = { println(_) }): Unit = {
    inTransaction { Session.currentSession.setLogger(logger) }
  }

  def stopLoggingSql(): Unit = { logSql(_ => Unit) }
}
