package org.chipmunk.entity

import org.scalatest.fixture

class KeyedSpec extends fixture.FlatSpec {
  // reflexivity
  "An EntityLike x" should "return true on equals(x)" in { f =>
    assert(f.ex == f.ex)
  }

  // symmetry
  it should "should return false on equals(y) if y is null" in { f =>
    assert(f.ex != null)
  }

  it should "should return true on equals(y) if y.equals(x) returns true" in { f =>
    assert(f.ey == f.ex && f.ex == f.ey)
  }

  it should "should return false on equals(y) if y.equals(x) returns false" in { f =>
    val ey = new EntityA(f.ex.value + 1)

    assert(ey != f.ex && f.ex != ey)
  }

  // transitivity
  it should "should return true on equals(z) if x.equals(y) and y.equals(z)" in { f =>
    assert(f.ex == f.ey && f.ey == f.ez && f.ex == f.ez)
  }

  protected def withFixture(test: OneArgTest) = {
    val value = 3
    val ex = new EntityA(value)
    val ey = new EntityA(value)
    val ez = new EntityA(value)

    val theFixture = FixtureParam(ex, ey, ez)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(ex: EntityA, ey: EntityA, ez: EntityA)

  class EntityA(val value: Int) extends Keyed {
    protected def keys: Product = Tuple1(value)
  }
}
