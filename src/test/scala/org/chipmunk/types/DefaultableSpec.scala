package org.chipmunk.types

import scala.annotation.implicitNotFound

import org.chipmunk.types.Defaultable.DefaultableInt
import org.chipmunk.types.Defaultable.defaultOf
import org.chipmunk.types.Defaultable.defaultableOption
import org.scalatest.FlatSpec

class DefaultableSpec extends FlatSpec {
  "Option[T]" should "have Some({T's default value}) as its default" in {
    val defaultTOpt = defaultOf[Option[TestType]]
    val defaultT = defaultOf[TestType]

    assert(defaultTOpt.isDefined)
    assert(defaultTOpt.get == defaultT)
  }

  case class TestType(x: Int)

  private implicit val defaultableTestType: Defaultable[TestType] =
    new Defaultable[TestType] {
      val defaultVal: TestType = TestType(defaultOf[Int])
    }
}
