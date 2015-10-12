package org.chipmunk.entity.relation.m2o

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Schema.animals
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.where
import org.squeryl.Table
import org.chipmunk.entity.relation.RelationHandleBehaviors
import org.scalatest.FlatSpec
import org.chipmunk.test.InMemoryDb
import org.chipmunk.TestSchema
import org.chipmunk.util.Configurator
import org.chipmunk.entity.relation.o2m.OneToManyHandle
import org.chipmunk.entity.Entity

class ManyToOneHandleSpec
    extends FlatSpec
    with TestSchema
    with InMemoryDb
    with RelationHandleBehaviors {

  "A ManyToOneHandle (of a new entity)" should behave like
    handleWithNonPersistedOwner(nonPersistedOwner, handleOf)

  it should behave like
    transientHandle(nonPersistedOwner, handleOf, toAdd, Seq())

  "A ManyToOneHandle (of a persisted entity)" should behave like
    handleWithPersistedOwner(persistedOwner, handleOf)

  it should behave like
    persistentOwneeHandle(persistedOwner, handleOf, persistedToAdd, counterpartHandleOf)

  private[this] def handleOf(owner: Animal): ManyToOneHandle[Animal] =
    owner.parent.asInstanceOf[ManyToOneHandle[Animal]]

  private[this] def counterpartHandleOf(owner: Animal): OneToManyHandle[Animal] =
    owner.children.asInstanceOf[OneToManyHandle[Animal]]

  private[this] def nonPersistedOwner = testAnimal("child")
  private[this] def persistedOwner = persist(nonPersistedOwner)

  private[this] def toAdd = testAnimal("parent")
  private[this] def persistedToAdd = { persist(toAdd) }

  private[this] def persist[E <: Entity[_]](e: E): E = {
    e.persist()
    e
  }

  private[this] def testAnimal(name: String): Animal =
    new Animal(name + "-" + System.currentTimeMillis)

  //  "A ManyToOneHandle" should "be creatable outside transactions" in { _ => }
  //
  //  it should "start in transient state when owning Entity is not persisted" in { f =>
  //    assert(f.ownersHandle.state.isTransient)
  //  }

  //  it should "start in persisted state when owning Entity is persisted" in withTransaction { f =>
  //    f.owner.persist()
  //    val ownersId = f.owner.id
  //
  //    val ownerFromDb = from(f.ownersTable) { s =>
  //      where(s.id === ownersId).select(s)
  //    }.head
  //    val ownerFromDbsHandle = f.testHandleOf(ownerFromDb)
  //
  //    assert(!ownerFromDbsHandle.state.isTransient)
  //  }

  //  it should "be relatable outside transactions" in { f =>
  //    f.ownersHandle += f.anotherE
  //  }
  //
  //  it should "be relatable outside transactions (with loops)" in { f =>
  //    f.ownersHandle += f.owner
  //  }
  //
  //  it should "be unrelatable outside transactions" in { f =>
  //    f.ownersHandle += f.anotherE
  //    f.ownersHandle.clear()
  //  }
  //
  //  it should "be unrelatable outside transactions (with loops)" in { f =>
  //    f.ownersHandle += f.owner
  //    f.ownersHandle.clear()
  //  }

  //  it should "be persistible when related if owner's body is persisted" in withTransaction { f =>
  //    f.ownersHandle += f.anotherE
  //    f.owner.persistBody()
  //    f.ownersHandle.persist()
  //  }
  //
  //  it should "be persistible when related (with loops) if owner's body is persisted" in withTransaction { f =>
  //    f.ownersHandle += f.owner
  //    f.owner.persistBody()
  //    f.ownersHandle.persist()
  //  }
  //
  //  it should "persist its related entities' bodies when persisted if owner's body is persisted" in withTransaction { f =>
  //    f.ownersHandle += f.anotherE
  //    f.owner.persistBody()
  //    f.ownersHandle.persist()
  //
  //    assert(f.anotherE.isPersisted)
  //  }

  //  protected def withFixture(test: OneArgTest) = {
  //    val owner = new Animal("Child")
  //    def testHandleOf(o: Animal): ManyToOneHandle[Animal] = {
  //      o.parent.asInstanceOf[ManyToOneHandle[Animal]]
  //    }
  //
  //    val anotherE = new Animal("A")
  //
  //    val theFixture = FixtureParam(animals, owner, testHandleOf, anotherE)
  //    withFixture(test.toNoArgTest(theFixture))
  //  }
  //
  //  case class FixtureParam(
  //      ownersTable: Table[Animal],
  //      owner: Animal,
  //      testHandleOf: Animal => ManyToOneHandle[Animal],
  //      anotherE: Animal) {
  //    val ownersHandle = testHandleOf(owner)
  //  }
}
