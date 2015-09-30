package org.chipmunk.entity.relation.m2m

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Schema.animals
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.where
import org.squeryl.Table
import org.chipmunk.util.Configurator
import org.chipmunk.entity.relation.RelationHandleBehaviors
import org.scalatest.FlatSpec
import org.chipmunk.test.InMemoryDb
import org.chipmunk.TestSchema

class ManyToManyHandleSpec
    extends FlatSpec
    with TestSchema
    with InMemoryDb
    with RelationHandleBehaviors {

  "A ManyToManyHandle (of a new entity)" should behave like
    handleWithNonPersistedOwner(nonPersistedOwner, handleOf)

  it should behave like
    transientHandle(nonPersistedOwner, handleOf, toAdd, Seq())

  "A ManyToManyHandle (of a persisted entity)" should behave like
    handleWithPersistedOwner(persistedOwner, handleOf)

  it should behave like persistentHandle(persistedOwner, handleOf, persistedToAdd)

  private[this] def handleOf(owner: Animal): ManyToManyHandle[Animal] = {
    owner.friends.asInstanceOf[ManyToManyHandle[Animal]]
  }

  private[this] def nonPersistedOwner = testAnimal("A")
  private[this] def persistedOwner = {
    val owner = nonPersistedOwner
    owner.persist()
    owner
  }

  private[this] def toAdd = testAnimal("B")
  private[this] def persistedToAdd = {
    val friend = toAdd
    friend.persist()
    friend
  }

  private[this] def testAnimal(name: String): Animal =
    new Animal(name + "-" + System.currentTimeMillis)

  //  "A ManyToManyHandle" should "be creatable outside transactions" in { _ => }
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
  //    f.ownersHandle -= f.anotherE
  //
  //    assert(f.ownersHandle forall { _ != f.anotherE })
  //  }
  //
  //  it should "be unrelatable outside transactions (with loops)" in { f =>
  //    f.ownersHandle += f.owner
  //    f.ownersHandle -= f.owner
  //
  //    assert(f.ownersHandle forall { _ != f.owner })
  //  }
  //
  //  it should "be clearable outside transactions" in { f =>
  //    f.ownersHandle += f.anotherE
  //    f.ownersHandle.clear()
  //  }
  //
  //  it should "be clearable outside transactions (with loops)" in { f =>
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
  //    val owner = new Animal("Owner")
  //    def testHandleOf(o: Animal): ManyToManyHandle[Animal] = {
  //      o.friends.asInstanceOf[ManyToManyHandle[Animal]]
  //    }
  //    val anotherE = new Animal("A")
  //
  //    val theFixture = FixtureParam(animals, owner, testHandleOf, anotherE)
  //    withFixture(test.toNoArgTest(theFixture))
  //  }
  //
  //  case class FixtureParam(
  //    ownersTable: Table[Animal],
  //    owner: Animal,
  //    testHandleOf: Animal => ManyToManyHandle[Animal],
  //    anotherE: Animal) {
  //    val ownersHandle = testHandleOf(owner)
  //  }
}
