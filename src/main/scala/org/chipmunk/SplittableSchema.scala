package org.chipmunk

import scala.collection.mutable
import org.squeryl.Schema
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.Table
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.dsl.{ Relation => SquerylRelation }

trait SplittableSchema extends Schema {
  private[this] var relationDeclarations = mutable.Buffer[DeclaredRelation[_]]()

  protected def declaration[R](constraints: Table[R] => Unit)(implicit manifestT: Manifest[R]): Table[R] = {
    val tbl = table[R]
    constraints(tbl)
    tbl
  }

  protected def relation[R <: SquerylRelation[_, _]](init: => R): DeclaredRelation[R] = {
    val declaration = new DeclaredRelation(init)
    relationDeclarations += declaration
    declaration
  }

  def initRelations(): Unit = {
    relationDeclarations foreach { _.init() }
  }
}

class DeclaredRelation[R <: SquerylRelation[_, _]](initRel: => R) {
  private[this] var _rel: Option[R] = _

  private[chipmunk] def init(): Unit = {
    _rel = Option(initRel)
    assume(_rel.isDefined, "Relation initialization failure")
  }

  private[chipmunk] def value: R = {
    assume(_rel.isDefined, "Relation not initialized")
    _rel.get
  }
}