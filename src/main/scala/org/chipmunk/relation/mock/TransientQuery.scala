package org.chipmunk.relation.mock

import org.squeryl.{ Query => SQuery }
import org.squeryl.internals.ResultSetMapper

abstract class TransientQuery[R] extends SQuery[R] {
  def iterable: Iterable[R]

  def iterator: Iterator[R] = iterable.iterator

  // Members declared in org.squeryl.Query
  def ast: org.squeryl.dsl.ast.ExpressionNode = ???

  def copy(asRoot: Boolean): org.squeryl.Query[R] = ???

  def distinct: SQuery[R] = ???

  def dumpAst: String = ???

  def forUpdate: SQuery[R] = ???

  def invokeYield(rsm: ResultSetMapper, resultSet: java.sql.ResultSet): R = ???

  def page(offset: Int, pageLength: Int): SQuery[R] = ???

  def statement: String = ???

  // Members declared in org.squeryl.Queryable
  def give(resultSetMapper: ResultSetMapper, rs: java.sql.ResultSet): R = ???

  def name: String = ???
}
