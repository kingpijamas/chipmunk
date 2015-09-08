package org.chipmunk.test.relation

import java.sql.ResultSet

import org.squeryl.{ Query => SQuery }
import org.squeryl.dsl.ast.ExpressionNode
import org.squeryl.internals.ResultSetMapper

abstract class Query[R] extends SQuery[R] {
  def iterable: Iterable[R]

  def iterator: Iterator[R] = iterable.iterator

  // Members declared in org.squeryl.Query
  def ast: ExpressionNode = ???

  def copy(asRoot: Boolean): SQuery[R] = ???

  def distinct: SQuery[R] = ???

  def dumpAst: String = ???

  def forUpdate: SQuery[R] = ???

  def invokeYield(rsm: ResultSetMapper, resultSet: ResultSet): R = ???

  def page(offset: Int, pageLength: Int): SQuery[R] = ???

  def statement: String = ???

  // Members declared in org.squeryl.Queryable
  def give(resultSetMapper: ResultSetMapper, rs: ResultSet): R = ???

  def name: String = ???
}
