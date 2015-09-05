package org.chipmunk.schema

import scala.collection.mutable

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.{ Association2 => Assoc2 }
import org.chipmunk.schema.Declaration.ManyToManyDeclaration
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.manyToManyRelation
import org.squeryl.PrimitiveTypeMode.oneToManyRelation
import org.squeryl.{ Schema => SquerylSchema }
import org.squeryl.Table
import org.squeryl.dsl.NumericalExpression
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ Relation => SquerylRelation }

trait Schema extends SquerylSchema {
  private[this] var relDeclarations = mutable.Buffer[Declaration[_]]()

  protected def declaration[R]
    (applyConstraints: Table[R] => Unit)
    (implicit ev: Manifest[R])
  : Table[R] = {
    val tbl = table[R]
    applyConstraints(tbl)
    tbl
  }

  protected def oneToMany[O <: Entity[_], M <: Entity[_]](
    oTable: => Table[O],
    mTable: => Table[M])
    (joinAttr: M => NumericalExpression[_])
    (unsetFk: M => Unit)
  : OneToManyDeclaration[O, M] = {
    declare(unsetFk) { oneToManyRelation(oTable, mTable).
      via((o: O, m: M) => o.id === joinAttr(m))
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    lTable: => Table[L],
    rTable: => Table[R],
    assocTableName: String)
  : ManyToManyDeclaration[L, R] = {
    declare { manyToManyRelation(lTable, rTable, assocTableName).
      via[Assoc2](manyToManyJoin)
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    lTable: => Table[L],
    rTable: => Table[R])
  : ManyToManyDeclaration[L, R] = {
    declare { manyToManyRelation(lTable, rTable).
      via[Assoc2](manyToManyJoin)
    }
  }

  private[this] def manyToManyJoin(l: Entity[_], r: Entity[_], a: Assoc2) =
    (l.id === a.ownerId, a.owneeId === r.id)

  private[this] def declare[R <: SquerylRelation[_, _]](
    rel: => R): Declaration[R] = {
    val declaration = new RegularDeclaration(rel)
    relDeclarations += declaration
    declaration
  }

  private[this] def declare[O <: Entity[_], M <: Entity[_]](
    unsetFk: M => Unit)(
    rel: => OneToManyRelation[O, M])
  : OneToManyDeclaration[O, M] = {
    val declaration = new OneToManyDeclaration(unsetFk, rel)
    relDeclarations += declaration
    declaration
  }

  protected def initRelations(): Unit = {
    relDeclarations foreach { _.init() }
  }
}
