package org.chipmunk.schema

import org.chipmunk.entity.Entity
import org.chipmunk.entity.relation.{ Association2 => Assoc2 }
import org.chipmunk.schema.Declaration.ManyToManyDeclaration
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.{ manyToManyRelation => squerylM2M }
import org.squeryl.PrimitiveTypeMode.{ oneToManyRelation => squerylO2M }
import org.squeryl.{ Schema => SquerylSchema }
import org.squeryl.Table
import org.squeryl.dsl.NumericalExpression

trait Schema extends SquerylSchema with LazyRelations with ForeignKeys {
  protected def declaration[R]
    (applyConstraints: Table[R] => Unit)
    (implicit ev: Manifest[R])
  : Table[R] = {
    val tbl = table[R]
    applyConstraints(tbl)
    tbl
  }

  protected def oneToMany[O <: Entity[_], M <: Entity[_], N <% NumericalExpression[_]](
    oTable: => Table[O],
    mTable: => Table[M])
    (fk: M => ForeignKey[N])
  : OneToManyDeclaration[O, M] = {
    declare(fk) {
      squerylO2M(oTable, mTable).
        via((o: O, m: M) => o.id === fk(m).value)
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    lTable: => Table[L],
    rTable: => Table[R],
    assocTableName: String)
  : ManyToManyDeclaration[L, R] = {
    declare {
      squerylM2M(lTable, rTable, assocTableName).
        via[Assoc2](m2mJoin)
    }
  }

  protected def manyToMany[L <: Entity[_], R <: Entity[_]](
    lTable: => Table[L],
    rTable: => Table[R])
  : ManyToManyDeclaration[L, R] = {
    declare {
      squerylM2M(lTable, rTable).
        via[Assoc2](m2mJoin)
    }
  }

  private[this] def m2mJoin(l: Entity[_], r: Entity[_], a: Assoc2) =
    (l.id === a.ownerId, a.owneeId === r.id)
}
