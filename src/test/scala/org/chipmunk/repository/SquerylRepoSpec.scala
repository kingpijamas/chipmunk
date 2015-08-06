package org.chipmunk.repository

import org.chipmunk.TestSchema
import org.chipmunk.TestSchema.Animal
import org.chipmunk.TestSchema.Schema
import org.chipmunk.test.InMemoryDb
import org.chipmunk.test.Transactions
import org.scalatest.Finders
import org.scalatest.Matchers
import org.scalatest.fixture
import org.chipmunk.DbSpec
import org.squeryl.Table

class SquerylRepoSpec extends DbSpec {
  "A SquerylRepo" should "fail when trying to save an entity from another table" in { f =>
    an[AssertionError] should be thrownBy f.repo.save(f.entity)
  }

  it should "fail when trying to remove an entity from another table" in { f =>
    an[AssertionError] should be thrownBy f.repo.remove(f.entity)
  }

  protected def withFixture(test: OneArgTest) = {
    // Table is not mockable, but at least this will do here
    // (since null will always be neq to any other non-null table)
    val anotherTable = null
    val entity = new Animal("Barky", table = anotherTable)
    val theFixture = FixtureParam(new AnimalRepo, entity)
    withFixture(test.toNoArgTest(theFixture))
  }

  case class FixtureParam(repo: AnimalRepo, entity: Animal)

  class AnimalRepo extends SquerylRepo[Animal] {
    val table = Schema.animals
  }
}
