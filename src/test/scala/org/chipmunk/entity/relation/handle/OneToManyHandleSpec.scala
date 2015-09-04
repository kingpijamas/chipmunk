package org.chipmunk.entity.relation.handle

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Schema.animals
import org.scalatest.Finders
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.where
import org.squeryl.Table

class OneToManyHandleSpec extends DbSpec {
  "A OneToManyHandle" should "be creatable outside transactions" in { _ => }

  it should "start in transient state when owning Entity is not persisted" in { f =>
    assert(f.ownersHandle.state.isTransient)
  }

  it should "start in persisted state when owning Entity is persisted" in withTransaction { f =>
    f.owner.persist()
    val ownersId = f.owner.id

    val ownerFromDb = from(f.ownersTable) { s =>
      where(s.id === ownersId).select(s)
    }.head
    val ownerFromDbsHandle = f.testHandleOf(ownerFromDb)

    assert(!ownerFromDbsHandle.state.isTransient)
  }

  it should "be relatable outside transactions when transient" in { f =>
    add(f.ownersHandle, f.anotherE)
  }
  it should "be relatable (with loops) outside transactions when transient" in { f =>
    add(f.ownersHandle, f.owner)
  }
  it should "be relatable in transactions when persistent" in withTransaction { f =>
    f.owner.persist()
    add(f.ownersHandle, f.anotherE)
  }
  it should "be relatable (with loops) in transactions when persistent" in withTransaction { f =>
    f.owner.persist()
    add(f.ownersHandle, f.owner)
  }
  def add(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
    handle += toAdd
    assert(handle exists { _ == toAdd })
  }

  it should "be unrelatable outside transactions when transient" in { f =>
    addThenRemove(f.ownersHandle, f.anotherE)
  }
  it should "be unrelatable in transactions when persistent" in withTransaction { f =>
    f.owner.persist()
    addThenRemove(f.ownersHandle, f.anotherE)
  }
  def addThenRemove(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
    handle += toAdd
    handle -= toAdd

    assert(handle forall { _ != toAdd })
  }

  it should "be clearable outside transactions when transient" in { f =>
    addThenClear(f.ownersHandle, f.anotherE)
  }
  it should "be clearable in transactions when persistent" in withTransaction { f =>
    f.owner.persist()
    addThenClear(f.ownersHandle, f.anotherE)
  }
  def addThenClear(handle: OneToManyHandle[Animal], toAdd: Animal): Unit = {
    handle += toAdd
    handle.clear()

    assert(handle.isEmpty)
  }

  it should "persist its related entities' bodies when persisted if owner's body is persisted" in withTransaction { f =>
    f.ownersHandle += f.anotherE
    f.owner.persistBody()
    f.ownersHandle.persist()

    assert(f.anotherE.isPersisted)
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new Animal("Owner")
    def testHandleOf(o: Animal): OneToManyHandle[Animal] = {
      o.children.asInstanceOf[OneToManyHandle[Animal]]
    }

    val anotherE = new Animal("A")

    val theFixture = FixtureParam(animals, owner, testHandleOf, anotherE)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(
      ownersTable: Table[Animal],
      owner: Animal,
      testHandleOf: Animal => OneToManyHandle[Animal],
      anotherE: Animal) {
    val ownersHandle = testHandleOf(owner)
  }
}
