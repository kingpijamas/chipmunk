package org.chipmunk.entity.relation

import org.chipmunk.TestSchema.Animal
import org.chipmunk.entity.Identifiable
import org.chipmunk.entity.relation.mock.Query
import org.mockito.Mockito.verify
import org.scalatest.Finders
import org.scalatest.fixture
import org.scalatest.mock.MockitoSugar
import org.squeryl.dsl.{ ManyToMany => SM2M }

class ManyToManySpec extends fixture.FlatSpec with MockitoSugar {
  "A ManyToMany" should "call 'associate' on add when isOwningSide" in { f =>
    val animal = new Animal
    val innerRel = f.ownerM2M.toSqueryl

    f.ownerM2M.add(animal)
    verify(innerRel).associate(animal)
  }

  it should "call 'assign' on add when not isOwningSide" in { f =>
    val animal = new Animal
    val innerRel = f.owneeM2M.toSqueryl

    f.owneeM2M.add(animal)
    verify(innerRel).assign(animal)
  }

  it should "call 'dissociate' on remove when isOwningSide" in { f =>
    checkDissociateCalledOnceOnRemove(f.ownerM2M)
  }

  it should "call 'dissociate' on remove when not isOwningSide" in { f =>
    checkDissociateCalledOnceOnRemove(f.owneeM2M)
  }

  private[this] def checkDissociateCalledOnceOnRemove(m2m: ManyToMany[Animal]) = {
    val animal = new Animal
    val innerRel = m2m.toSqueryl

    m2m.remove(animal)
    verify(innerRel).dissociate(animal)
  }

  it should "call 'dissociateAll' on removeAll when isOwningSide" in { f =>
    checkDissociateAllCalledOnceOnRemove(f.ownerM2M)
  }

  it should "call 'dissociateAll' on removeAll when not isOwningSide" in { f =>
    checkDissociateAllCalledOnceOnRemove(f.owneeM2M)
  }

  private[this] def checkDissociateAllCalledOnceOnRemove(m2m: ManyToMany[Animal]) = {
    val innerRel = m2m.toSqueryl

    m2m.removeAll()
    verify(innerRel).dissociateAll
  }

  protected def withFixture(test: OneArgTest) = {
    val owner = new TestManyToMany(true)
    val ownee = new TestManyToMany(false)
    val fixture = FixtureParam(owner, ownee)
    withFixture(test.toNoArgTest(fixture))
  }

  case class FixtureParam(ownerM2M: ManyToMany[Animal], owneeM2M: ManyToMany[Animal])

  class TestManyToMany(val isOwningSide: Boolean) extends ManyToMany[Animal] {
    val toSqueryl: SRel = mock[TestSM2M[Animal]]
  }

  class TestSM2M[O <: Identifiable] extends Query[O] with SM2M[O, Association2] {
    def assign(o: O): Association2 = ???
    def assign(o: O, a: Association2): Association2 = ???
    def associate(o: O): Association2 = ???
    def associate(o: O, a: Association2): Association2 = ???
    def associationMap: org.squeryl.Query[(O, Association2)] = ???
    def associations: org.squeryl.Query[Association2] = ???
    def dissociate(o: O): Boolean = ???
    def dissociateAll: Int = ???
    def iterable: Iterable[O] = ???
  }
}
