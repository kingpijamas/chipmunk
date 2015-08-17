package org.chipmunk.entity.relation.handle

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.scalatest.Finders

class ManyToManyHandleSpec extends DbSpec {
  "A ManyToManyProxy" should "be creatable outside transactions" in { _ => }

  it should "be relatable outside transactions" in { f =>
    f.m2mProxy.add(f.anotherE)
  }

  it should "be relatable outside transactions (with loops)" in { f =>
    f.m2mProxy.add(f.owner)
  }

  it should "be unrelatable outside transactions" in { f =>
    f.m2mProxy.add(f.anotherE)
    f.m2mProxy.removeAll()
  }

  it should "be unrelatable outside transactions (with loops)" in { f =>
    f.m2mProxy.add(f.owner)
    f.m2mProxy.removeAll()
  }

  it should "be persistible when related if owner's body is persisted" in withTransaction { f =>
    f.m2mProxy.add(f.anotherE)
    f.owner.persistBody()
    f.m2mProxy.persist()
  }

  it should "be persistible when related (with loops) if owner's body is persisted" in withTransaction { f =>
    f.m2mProxy.add(f.owner)
    f.owner.persistBody()
    f.m2mProxy.persist()
  }

  it should "persist its related entities' bodies when persisted if owner's body is persisted" in withTransaction { f =>
    f.m2mProxy.add(f.anotherE)
    f.owner.persistBody()
    f.m2mProxy.persist()

    assert(f.anotherE.isPersisted)
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new Animal("Owner")
    val o2mProxy = owner.friends.asInstanceOf[ManyToManyHandle[Animal]]

    val anotherE = new Animal("A")

    val theFixture = FixtureParam(owner, o2mProxy, anotherE)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(
    owner: Animal,
    m2mProxy: ManyToManyHandle[Animal],
    anotherE: Animal)
}
