package org.chipmunk.entity.relation.proxy

import org.chipmunk.DbSpec
import org.chipmunk.TestSchema.Animal
import org.chipmunk.entity.relation.proxy.ManyToOneProxy.ManyToOneProxy

class ManyToOneProxySpec extends DbSpec {
  "A ManyToOneProxy" should "be creatable outside transactions" in { _ => }

  it should "be relatable outside transactions" in { f =>
    f.m2oProxy.add(f.anotherE)
  }

  it should "be relatable outside transactions (with loops)" in { f =>
    f.m2oProxy.add(f.owner)
  }

  it should "be unrelatable outside transactions" in { f =>
    f.m2oProxy.add(f.anotherE)
    f.m2oProxy.removeAll()
  }

  it should "be unrelatable outside transactions (with loops)" in { f =>
    f.m2oProxy.add(f.owner)
    f.m2oProxy.removeAll()
  }

  it should "be persistible when related if owner's body is persisted" in withTransaction { f =>
    f.m2oProxy.add(f.anotherE)
    f.owner.persistBody()
    f.m2oProxy.persist()
  }

  it should "be persistible when related (with loops) if owner's body is persisted" in withTransaction { f =>
    f.m2oProxy.add(f.owner)
    f.owner.persistBody()
    f.m2oProxy.persist()
  }

  it should "persist its related entities' bodies when persisted if owner's body is persisted" in withTransaction { f =>
    f.m2oProxy.add(f.anotherE)
    f.owner.persistBody()
    f.m2oProxy.persist()

    assert(f.anotherE.isPersisted)
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new Animal("Child")
    val o2mProxy = owner.parent.asInstanceOf[ManyToOneProxy[Animal]]

    val anotherE = new Animal("A")

    val theFixture = FixtureParam(owner, o2mProxy, anotherE)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(
    owner: Animal,
    m2oProxy: ManyToOneProxy[Animal],
    anotherE: Animal)
}
