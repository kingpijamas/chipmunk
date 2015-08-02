package org.chipmunk.persistent

import org.chipmunk.Identifiable.Id
import org.chipmunk.SplittableSchema
import org.chipmunk.persistent.Defaultable.DefaultableLong
import org.chipmunk.persistent.Defaultable.defaultOf
import org.scalatest.Suite
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.optionLong2ScalarLong
import org.squeryl.PrimitiveTypeMode.string2ScalarString
import org.squeryl.Schema
import org.chipmunk.test.InMemoryDb

trait TestSchema {
  self: Suite =>

  protected def testSchema: Schema = TestSchema.Schema
}

object TestSchema {
  val DogSpeciesName = "Canis familiaris"

  object Schema extends SplittableSchema {
    val animals = declaration[Animal] { tbl =>
      on(tbl)(s => declare(
        s.id is (autoIncremented),
        columns(s.name) are (unique)))
    }

    val species = declaration[Species] { tbl =>
      on(tbl)(s => declare(
        s.id is (autoIncremented),
        columns(s.name) are (unique)))
    }

    val habitats = declaration[Habitat] { tbl =>
      on(tbl)(s => declare(
        s.id is (autoIncremented),
        columns(s.name) are (unique)))
    }

    val species2Animals = oneToMany(species, animals) { _.speciesId }

    val parent2Children = oneToMany(animals, animals) { _.parentId }

    val friends = manyToMany(animals, animals, "mates")

    val animals2Habitats = manyToMany(animals, habitats, "animals2Habitats")

    initRelations()
  }

  class Animal(
    val name: String,
    var speciesId: Option[Id] = None,
    var parentId: Option[Id] = None)
      extends Entity[Animal](Schema.animals) {
    def this() = this(defaultOf[String], defaultOf[Option[Id]], defaultOf[Option[Id]])

    def keys: Product1[String] = Tuple1(name)

    lazy val habitats = owner(Schema.animals2Habitats)

    lazy val species = ownee(Schema.species2Animals)

    lazy val parent = ownee(Schema.parent2Children)

    lazy val children = owner(Schema.parent2Children)

    lazy val friends = owner(Schema.friends)

    def addChildren(children: Animal*): Unit = {
      this.children.add(children: _*)
      children foreach { _.parent.toSqueryl.assign(this) }
    }

    def addFriends(friends: Animal*): Unit = {
      this.friends.add(friends: _*)
    }
  }

  class Species(val name: String) extends Entity[Species](Schema.species) {
    def keys: Product1[String] = Tuple1(name)

    lazy val animals = owner(Schema.species2Animals)

    def add(animal: Animal): Unit = {
      animals.add(animal)
      animal.species.toSqueryl.assign(this)
    }
  }

  class Habitat(val name: String)
      extends Entity[Habitat](Schema.habitats) {
    def keys: Product1[String] = Tuple1(name)

    lazy val animals = ownee(Schema.animals2Habitats)
  }
}
