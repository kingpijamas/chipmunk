package org.chipmunk.schema

import org.chipmunk.entity.Entity
import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.relation.m2m.Association2
import org.chipmunk.util.Validations.assumeState
import org.squeryl.dsl.ManyToManyRelation
import org.squeryl.dsl.OneToManyRelation
import org.squeryl.dsl.{ Relation => SquerylRelation }

object Declaration {
  type ManyToOneDeclaration[M <: Entity[_], O <: Entity[_]] =
    OneToManyDeclaration[O, M]

  type ManyToManyDeclaration[L <: Identifiable, R <: Identifiable] =
    Declaration[ManyToManyRelation[L, R, Association2]]
}

trait Declaration[R <: SquerylRelation[_, _]] {
  private[this] var relOpt: Option[R] = None

  private[schema] def _rel: R

  private[chipmunk] def init(): Unit = {
    relOpt = Option(_rel)
    assumeState(relOpt.isDefined, "Relation initialization failure")
  }

  private[chipmunk] def value: R = {
    assumeState(relOpt.isDefined, "Relation not initialized")
    relOpt.get
  }
}

class RegularDeclaration[R <: SquerylRelation[_, _]](rel: => R)
  extends Declaration[R] {
  lazy val _rel = rel
}

class OneToManyDeclaration[O <: Entity[_], M <: Entity[_]](
  private[chipmunk] val fk: M => ForeignKey[_],
  rel: => OneToManyRelation[O, M])
    extends Declaration[OneToManyRelation[O, M]] {
  lazy val _rel = rel
}
