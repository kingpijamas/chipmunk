package org.chipmunk.persistent

import scala.annotation.implicitNotFound

import org.chipmunk.persistent.Defaultable.defaultableOption
import org.scalatest.Finders
import org.scalatest.FlatSpec

class DefaultableSpec extends FlatSpec {
  "Option[T]" should "have Some({T's default value}) as its default" in {
    val defaultTOpt: Option[TestType] = defaultOf[Option[TestType]]
    val defaultT: TestType = defaultOf[TestType]

    assert(defaultTOpt.isDefined)
    assert(defaultTOpt.get == defaultT)
  }

  private def defaultOf[T: Defaultable]: T =
    implicitly[Defaultable[T]].defaultVal

  private case class TestType(x: Int)

  private implicit val defaultableTestType: Defaultable[TestType] =
    new Defaultable[TestType] {
      val defaultVal: TestType = TestType(0)
    }
}
