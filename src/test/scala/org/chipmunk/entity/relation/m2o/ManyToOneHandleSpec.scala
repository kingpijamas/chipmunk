package org.chipmunk.entity.relation.m2o

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Schema.animals
import org.squeryl.PrimitiveTypeMode.from
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.where
import org.squeryl.Table

class ManyToOneHandleSpec extends DbSpec {
  "A ManyToOneHandle" should "be creatable outside transactions" in { _ => }

  it should "start in transient state when owning Entity is not persisted" in { f =>
    assert(f.ownersHandle.state.isTransient)
  }

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

  it should "be relatable outside transactions" in { f =>
    f.ownersHandle += f.anotherE
  }

  it should "be relatable outside transactions (with loops)" in { f =>
    f.ownersHandle += f.owner
  }

  it should "be unrelatable outside transactions" in { f =>
    f.ownersHandle += f.anotherE
    f.ownersHandle.clear()
  }

  it should "be unrelatable outside transactions (with loops)" in { f =>
    f.ownersHandle += f.owner
    f.ownersHandle.clear()
  }

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

  protected def withFixture(test: OneArgTest) = {
    val owner = new Animal("Child")
    def testHandleOf(o: Animal): ManyToOneHandle[Animal] = {
      o.parent.asInstanceOf[ManyToOneHandle[Animal]]
    }

    val anotherE = new Animal("A")

    val theFixture = FixtureParam(animals, owner, testHandleOf, anotherE)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(
      ownersTable: Table[Animal],
      owner: Animal,
      testHandleOf: Animal => ManyToOneHandle[Animal],
      anotherE: Animal) {
    val ownersHandle = testHandleOf(owner)
  }
}
