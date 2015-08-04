package org.chipmunk.entity

import org.scalamock.scalatest.MockFactory
import org.scalatest.FlatSpec

class KeyedSpec extends FlatSpec with MockFactory {
  private[this] val SomeNumber = 3

  // reflexivity
  "An EntityLike x" should "return true on equals(x)" in {
    val ex = new EntityA(SomeNumber)

    assert(ex == ex)
  }

  // symmetry
  it should "should return false on equals(y) if y is null" in {
    val ex = new EntityA(SomeNumber)

    assert(ex != null)
  }

  it should "should return true on equals(y) if y.equals(x) returns true" in {
    val ex = new EntityA(SomeNumber)
    val ey = new EntityA(SomeNumber)

    assert(ey == ex && ex == ey)
  }

  it should "should return false on equals(y) if y.equals(x) returns false" in {
    val ex = new EntityA(SomeNumber)
    val ey = new EntityA(SomeNumber + 1)

    assert(ey != ex && ex != ey)
  }

  // transitivity
  it should "should return true on equals(z) if x.equals(y) and y.equals(z)" in {
    val ex = new EntityA(SomeNumber)
    val ey = new EntityA(SomeNumber)
    val ez = new EntityA(SomeNumber)

    assert(ex == ey && ey == ez && ex == ez)
  }

  private[this] class EntityA(val x: Int) extends Keyed {
    protected def keys: Product = Tuple1(x)
  }
}
