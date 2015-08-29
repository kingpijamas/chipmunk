package org.chipmunk.schema

import scala.collection.mutable

import org.chipmunk.entity.Entity
import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.relation.Association2
import org.chipmunk.schema.SplittableSchema.ManyToManyDeclaration
import org.chipmunk.schema.SplittableSchema.OneToManyDeclaration
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.manyToManyRelation
import org.squeryl.PrimitiveTypeMode.oneToManyRelation
import org.squeryl.Schema
import org.squeryl.Table
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.NumericalExpression
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ Relation => SquerylRelation }

object SplittableSchema {
  type OneToManyDeclaration[O <: Identifiable, M] =
    Declaration[OneToManyRelation[O, M]]

  type ManyToOneDeclaration[M, O <: Identifiable] = OneToManyDeclaration[O, M]

  type ManyToManyDeclaration[L <: Identifiable, R <: Identifiable] =
    Declaration[ManyToManyRelation[L, R, Association2]]
}

trait SplittableSchema extends Schema {
  private[this] var relDeclarations = mutable.Buffer[Declaration[_]]()

  protected def declaration[R]
    (constraints: Table[R] => Unit)
    (implicit ev: Manifest[R])
  : Table[R] = {
    val tbl = table[R]
    constraints(tbl)
    tbl
  }

  protected def oneToMany[O <: Entity[_], M](
    tableOfO: => Table[O],
    tableOfM: => Table[M])
    (joinAttr: M => NumericalExpression[_])
  : OneToManyDeclaration[O, M] = {
    declare {
      oneToManyRelation(tableOfO, tableOfM).
        via((o: O, m: M) => o.id === joinAttr(m))
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    tableOfL: => Table[L],
    tableOfR: => Table[R],
    tableName: String)
  : ManyToManyDeclaration[L, R] = {
    declare {
      manyToManyRelation(tableOfL, tableOfR, tableName).
       via[Association2](manyToManyJoin)
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    tableOfL: => Table[L],
    tableOfR: => Table[R])
  : ManyToManyDeclaration[L, R] = {
    declare {
      manyToManyRelation(tableOfL, tableOfR).
       via[Association2](manyToManyJoin)
    }
  }

  private[this] def manyToManyJoin(l: Entity[_], r: Entity[_], a: Association2) =
    (l.id === a.ownerId, a.owneeId === r.id)

  private[this] def declare[R <: SquerylRelation[_, _]](
    rel: => R): Declaration[R] = {
    val declaration = new Declaration(rel)
    relDeclarations += declaration
    declaration
  }

  protected def initRelations(): Unit = {
    relDeclarations foreach { _.init() }
  }
}

class Declaration[R <: SquerylRelation[_, _]](rel: => R) {
  private[this] var _rel: Option[R] = None

  private[chipmunk] def init(): Unit = {
    _rel = Option(rel)
    assume(_rel.isDefined, "Relation initialization failure")
  }

  private[chipmunk] def value: R = {
    assume(_rel.isDefined, "Relation not initialized")
    _rel.get
  }
}
