package org.chipmunk

import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.manyToManyRelation
import org.squeryl.PrimitiveTypeMode.oneToManyRelation
import scala.collection.mutable
import org.squeryl.Schema
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.Table
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.ast.EqualityExpression

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

  protected def manyToMany[L <: persistent.Entity[_], R <: persistent.Entity[_]](
    tableL: Table[L], tableR: Table[R], nameOfMiddleTable: String): DeclaredRelation[SquerylRelation[L, R]] = {
    relation {
      manyToManyRelation(tableL, tableR, nameOfMiddleTable).via[BinaryAssociation](
        (l, r, a) => (l.id === a.ownerId, r.id === a.owneeId))
    }
  }

  protected def oneToMany[O <: persistent.Entity[_], M](
    tableO: Table[O], tableM: Table[M], f: (O, M) => EqualityExpression): DeclaredRelation[SquerylRelation[O, M]] = {
    relation {
      oneToManyRelation(tableO, tableM).via(f)
    }
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