package org.chipmunk.entity.relation

import org.chipmunk.TestSchema.Animal
import org.chipmunk.entity.Identifiable
import org.chipmunk.test.relation.Query
import org.mockito.Mockito.verify
import org.scalatest.Finders
import org.scalatest.fixture
import org.scalatest.mock.MockitoSugar
import org.squeryl.dsl.{ ManyToOne => SM2O }
import org.squeryl.KeyedEntity

class ManyToOneSpec extends fixture.FlatSpec with MockitoSugar {
  "A ManyToOne" should "call 'assign' on +=" in { f =>
    val animal = new Animal
    val innerRel = f.m2o.toSqueryl

    f.m2o += animal
    verify(innerRel).assign(animal)
  }

  it should "call 'delete' on clear" in { f =>
    val animal = new Animal
    val innerRel = f.m2o.toSqueryl

    f.m2o.clear()
    verify(innerRel).delete
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new TestManyToOne
    val fixture = FixtureParam(owner)
    withFixture(test.toNoArgTest(fixture))
  }

  case class FixtureParam(m2o: ManyToOne[Animal])

  class TestManyToOne extends ManyToOne[Animal] {
    val toSqueryl: SRel = mock[TestSM2O[Animal]]
  }

  class TestSM2O[O <: Identifiable] extends Query[O] with SM2O[O] {
    def assign(one: O): O = ???
    def delete: Boolean = ???
    def iterable: Iterable[O] = ???
  }
}
