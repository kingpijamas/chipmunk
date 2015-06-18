package org.chipmunk.mock

import org.squeryl.Query
import org.squeryl.internals.ResultSetMapper

abstract class MockQuery[R] extends Query[R] {
  def iterable: Iterable[R]

  def iterator: Iterator[R] = iterable.iterator

  // Members declared in org.squeryl.Query
  def ast: org.squeryl.dsl.ast.ExpressionNode = ???

  def copy(asRoot: Boolean): org.squeryl.Query[R] = ???

  def distinct: Query[R] = ???

  def dumpAst: String = ???

  def forUpdate: Query[R] = ???

  def invokeYield(rsm: ResultSetMapper, resultSet: java.sql.ResultSet): R = ???

  def page(offset: Int, pageLength: Int): Query[R] = ???

  def statement: String = ???

  // Members declared in org.squeryl.Queryable
  def give(resultSetMapper: ResultSetMapper, rs: java.sql.ResultSet): R = ???

  def name: String = ???
}