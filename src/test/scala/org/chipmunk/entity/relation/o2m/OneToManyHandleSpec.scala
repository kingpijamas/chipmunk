package org.chipmunk.entity.relation.o2m

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

class OneToManyHandleSpec
    extends FlatSpec
    with TestSchema
    with InMemoryDb
    with RelationHandleBehaviors {

  "A OneToManyHandle (of a new entity)" should behave like
    handleWithNonPersistedOwner(nonPersistedOwner, handleOf)

  it should behave like
    transientHandle(nonPersistedOwner, handleOf, toAdd, Seq())

  "A OneToManyHandle (of a persisted entity)" should behave like
    handleWithPersistedOwner(persistedOwner, handleOf)

  it should behave like persistentHandle(persistedOwner, handleOf, toAdd)

  private[this] def handleOf(owner: Animal): OneToManyHandle[Animal] = {
    owner.children.asInstanceOf[OneToManyHandle[Animal]]
  }

  private[this] def nonPersistedOwner = testAnimal("owner")

  private[this] def persistedOwner = {
    val owner = testAnimal("owner")
    owner.persist()
    owner
  }

  private[this] def toAdd = testAnimal("toAdd")

  private[this] def testAnimal(name: String): Animal =
    new Animal(name + "-" + System.currentTimeMillis)

  //  it should "be relatable outside transactions when transient" in { f =>
  //    add(f.ownersHandle, f.anotherE)
  //  }
  //  it should "be relatable (with loops) outside transactions when transient" in { f =>
  //    add(f.ownersHandle, f.owner)
  //  }
  //  it should "be relatable in transactions when persistent" in withTransaction { f =>
  //    f.owner.persist()
  //    add(f.ownersHandle, f.anotherE)
  //  }
  //  it should "be relatable (with loops) in transactions when persistent" in withTransaction { f =>
  //    f.owner.persist()
  //    add(f.ownersHandle, f.owner)
  //  }
  //  def add(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
  //    handle += toAdd
  //    assert(handle exists { _ == toAdd })
  //  }
  //
  //  it should "be unrelatable outside transactions when transient" in { f =>
  //    addThenRemove(f.ownersHandle, f.anotherE)
  //  }
  //  it should "be unrelatable in transactions when persistent" in withTransaction { f =>
  //    f.owner.persist()
  //    addThenRemove(f.ownersHandle, f.anotherE)
  //  }
  //  def addThenRemove(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
  //    handle += toAdd
  //    handle -= toAdd
  //
  //    assert(handle forall { _ != toAdd })
  //  }
  //
  //  it should "be clearable outside transactions when transient" in { f =>
  //    addThenClear(f.ownersHandle, f.anotherE)
  //  }
  //  it should "be clearable in transactions when persistent" in withTransaction { f =>
  //    f.owner.persist()
  //    addThenClear(f.ownersHandle, f.anotherE)
  //  }
  //  def addThenClear(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
  //    handle += toAdd
  //    handle.clear()
  //
  //    assert(handle.isEmpty)
  //  }
  //
  //  it should "persist its related entities' bodies when persisted if owner's body is persisted" in withTransaction { f =>
  //    f.ownersHandle += f.anotherE
  //    f.owner.persistBody()
  //    f.ownersHandle.persist()
  //
  //    assert(f.anotherE.isPersisted)
  //  }
  //
  //  protected def withFixture(test: OneArgTest) = {
  //    val owner = new Animal("Owner")
  //    def testHandleOf(o: Animal): OneToManyHandle[Animal] = {
  //      o.children.asInstanceOf[OneToManyHandle[Animal]]
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
  //      testHandleOf: Animal => OneToManyHandle[Animal],
  //      anotherE: Animal) {
  //    val ownersHandle = testHandleOf(owner)
  //  }
}
