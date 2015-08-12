package org.chipmunk.value

import scala.annotation.implicitNotFound

import org.chipmunk.value.Defaultable.DefaultableInt;
import org.chipmunk.value.Defaultable.defaultOf;
import org.chipmunk.value.Defaultable.defaultableOption;
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
