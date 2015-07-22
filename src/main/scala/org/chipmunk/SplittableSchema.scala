package org.chipmunk

import scala.collection.mutable
import org.chipmunk.Identifiable.Id
import org.chipmunk.persistent.Association2
import org.chipmunk.persistent.Entity
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.manyToManyRelation
import org.squeryl.PrimitiveTypeMode.oneToManyRelation
import org.squeryl.Schema
import org.squeryl.Table
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ Relation => SquerylRelation }
import org.squeryl.dsl.ast.EqualityExpression

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

  protected def oneToMany[O <: persistent.Entity[_], M](
    getTableO: SplittableSchema.this.type => Table[O],
    getTableM: SplittableSchema.this.type => Table[M])
    (f: (O, M) => EqualityExpression)
  : Declaration[OneToManyRelation[O, M]] = {
    declare {
      oneToManyRelation(getTableO(this), getTableM(this)).via(f)
    }
  }
  
  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    getTableL: this.type => Table[L],
    getTableR: this.type => Table[R],
    nameOfAssocTable: String)
  : Declaration[ManyToManyRelation[L, R, Association2]] = {
    declare {
      manyToManyRelation(getTableL(this), getTableR(this), nameOfAssocTable).
       via[Association2](manyToManyJoin)
    }
  }

  private[this] def manyToManyJoin =
    (l: Entity[_], r: Entity[_], a: Association2) =>
      (l.id === a.ownerId, a.owneeId === r.id)

  private[this] def declare[R <: SquerylRelation[_, _]](
      rel: => R)
  : Declaration[R] = {
    val declaration = new Declaration(rel)
    relDeclarations += declaration
    declaration
  }

  protected def initRelations(): Unit = {
    relDeclarations foreach { _.init() }
  }
}

class Declaration[R <: SquerylRelation[_, _]](rel: => R) {
  private[this] var _rel: Option[R] = _

  private[chipmunk] def init(): Unit = {
    _rel = Option(rel)
    assume(_rel.isDefined, "Relation initialization failure")
  }

  private[chipmunk] def value: R = {
    assume(_rel.isDefined, "Relation not initialized")
    _rel.get
  }
}
