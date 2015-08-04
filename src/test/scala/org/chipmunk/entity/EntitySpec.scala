package org.chipmunk.entity

import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Species
import org.chipmunk.test.InMemoryDb
import org.scalatest.FlatSpec
import org.scalatest.fixture
import org.chipmunk.test.Transactions
import org.chipmunk.TestSchema

class EntitySpec extends fixture.FlatSpec with TestSchema with Transactions with InMemoryDb {
  case class FixtureParam(dogSpecies: Species, dogX: Animal, dogY: Animal, dogZ: Animal)

  "An Entity x" should "be creatable outside transactions" in { f => }

  it should "be relatable outside transactions" in { f =>
    f.dogSpecies.add(f.dogX)
  }

  it should "be persistible when unrelated" in withTransaction { f =>
    f.dogSpecies.persist()

    assert(f.dogSpecies.isPersisted)
  }

  it should "be persistible when related (OneToMany)" in withTransaction { f =>
    f.dogSpecies.add(f.dogX)
    f.dogSpecies.persist()

    assert(f.dogSpecies.isPersisted)
  }

  it should "persist its owned related entities when persisted (x-OneToMany-y, y!=x)" in withTransaction { f =>
    f.dogSpecies.add(f.dogX)
    f.dogSpecies.persist()

    assert(f.dogX.isPersisted)
  }

  it should "persist its owned related entities when persisted (x-OneToMany-x)" in withTransaction { f =>
    // because hey, how else would there be actual dogs in this model, right?
    f.dogX.addChildren(f.dogX)
    f.dogX.persist()

    assert(f.dogX.isPersisted)
  }

  it should "be persistible when related (ManyToMany)" in withTransaction { f =>
    f.dogX.addFriends(f.dogY, f.dogZ)
    f.dogX.persist()

    assert(f.dogX.isPersisted)
  }

  it should "persist its owned related entities when persisted (x-ManyToMany-{y,z}, y!=x, z!=x)" in withTransaction { f =>
    f.dogX.addFriends(f.dogY, f.dogZ)
    f.dogX.persist()

    assert(f.dogY.isPersisted)
    assert(f.dogZ.isPersisted)
  }

  it should "persist its owned related entities when persisted (x-ManyToMany-{x,y}, y!=x)" in withTransaction { f =>
    f.dogX.addFriends(f.dogY, f.dogX)
    f.dogX.persist()

    assert(f.dogY.isPersisted)
    assert(f.dogX.isPersisted)
  }

  protected def withFixture(test: OneArgTest) = {
    val dogSpecies = new Species("Canis familiaris") //because we are old school here
    val barky = new Animal("Barky")
    val barkette = new Animal("Barkette")
    val barko = new Animal("Barko")

    val theFixture = FixtureParam(dogSpecies, barky, barkette, barko)
    withFixture(test.toNoArgTest(theFixture))
  }
}
