package org.chipmunk.entity.relation

import org.chipmunk.TestSchema.Animal
import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.relation.mock.Query
import org.mockito.Mockito.verify
import org.scalatest.Finders
import org.scalatest.fixture
import org.scalatest.mock.MockitoSugar
import org.squeryl.dsl.{ OneToMany => SO2M }
import org.squeryl.KeyedEntity

class OneToManySpec extends fixture.FlatSpec with MockitoSugar {
  "A OneToMany" should "call 'associate' on +=" in { f =>
    val animal = new Animal
    val innerRel = f.o2m.toSqueryl

    f.o2m += animal
    verify(innerRel).associate(animal)
  }

  it should "call 'deleteAll' on clear" in { f =>
    val animal = new Animal
    val innerRel = f.o2m.toSqueryl

    f.o2m.clear()
    verify(innerRel).deleteAll
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new TestOneToMany
    val fixture = FixtureParam(owner)
    withFixture(test.toNoArgTest(fixture))
  }

  case class FixtureParam(o2m: OneToMany[Animal])

  class TestOneToMany extends OneToMany[Animal] {
    val toSqueryl: SRel = mock[TestSO2M[Animal]]

    def -=(other: Animal): this.type = ???
  }

  class TestSO2M[O <: Identifiable] extends Query[O] with SO2M[O] {
    def assign(m: O): O = ???
    def associate(m: O)(implicit ev: <:<[O, KeyedEntity[_]]): O = ???
    def deleteAll: Int = ???
    def iterable: Iterable[O] = ???
  }
}
