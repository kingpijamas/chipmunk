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
import org.squeryl.dsl.OneToManyRelation

trait SplittableSchema extends Schema {
  private[this] var relationDeclarations = mutable.Buffer[DeclaredRelation[_]]()

  protected def declaration[R](constraints: Table[R] => Unit)(implicit manifestT: Manifest[R]): Table[R] = {
    val tbl = table[R]
    constraints(tbl)
    tbl
  }

  protected def oneToMany[O <: persistent.Entity[_], M](
    getTableO: SplittableSchema.this.type => Table[O],
    getTableM: SplittableSchema.this.type => Table[M])
    (f: (O, M) => EqualityExpression)
  : DeclaredRelation[OneToManyRelation[O, M]] = {
    addRelation {
      oneToManyRelation(getTableO(this), getTableM(this)).via(f)
    }
  }

  protected def manyToMany[L <: persistent.Entity[_], R <: persistent.Entity[_]](
    getTableL: SplittableSchema.this.type => Table[L],
    getTableR: SplittableSchema.this.type => Table[R],
    nameOfMiddleTable: String)
  : DeclaredRelation[ManyToManyRelation[L, R, BinaryAssociation]] = {
    addRelation {
      manyToManyRelation(getTableL(this), getTableR(this), nameOfMiddleTable).via[BinaryAssociation](
        (left, right, assoc) => (left.id === assoc.ownerId, assoc.owneeId === right.id))
    }
  }

  private[this] def addRelation[R <: SquerylRelation[_, _]](init: => R): DeclaredRelation[R] = {
    val declaration = new DeclaredRelation(init)
    relationDeclarations += declaration
    declaration
  }

  protected def initRelations(): Unit = {
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
