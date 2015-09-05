package org.chipmunk

import org.chipmunk.entity.Entity
import org.chipmunk.entity.Identifiable.Id
import org.chipmunk.value.Defaultable.DefaultableLong
import org.chipmunk.value.Defaultable.defaultOf
import org.scalatest.Finders
import org.scalatest.Suite
import org.squeryl.PrimitiveTypeMode.long2ScalarLong
import org.squeryl.PrimitiveTypeMode.optionLong2ScalarLong
import org.squeryl.PrimitiveTypeMode.string2ScalarString
import org.squeryl.Table
import org.chipmunk.schema.{ Schema => ChipmunkSchema }
import org.chipmunk.repository.SquerylRepo

trait TestSchema {
  self: Suite =>

  protected def testSchema = TestSchema.Schema
}

object TestSchema {
  object Schema extends ChipmunkSchema {
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

    val species2Animals = oneToMany(species, animals) { _.speciesId } { _.speciesId = None }

    val parent2Children = oneToMany(animals, animals) { _.parentId }  { _.parentId = None }

    val friends = manyToMany(animals, animals, "mates")

    val animals2Habitats = manyToMany(animals, habitats, "animals2Habitats")

    initRelations()
  }

  class Animal(
    val name: String,
    var speciesId: Option[Id] = None,
    var parentId: Option[Id] = None,
    table: Table[Animal] = Schema.animals)
      extends Entity[Animal](table) {
    def this() = this(
      defaultOf[String],
      defaultOf[Option[Id]],
      defaultOf[Option[Id]])

    def keys: Product1[String] = Tuple1(name)

    lazy val habitats = owner(Schema.animals2Habitats)

    lazy val species = ownee(Schema.species2Animals)

    lazy val parent = ownee(Schema.parent2Children)

    lazy val children = owner(Schema.parent2Children)

    lazy val friends = owner(Schema.friends)

    def addChildren(children: Animal*): Unit = {
      this.children ++= children
      children foreach { _.parent += this }
    }

    def addFriends(friends: Animal*): Unit = {
      this.friends ++= friends
    }
  }

  class Species(val name: String) extends Entity[Species](Schema.species) {
    def keys: Product1[String] = Tuple1(name)

    lazy val animals = owner(Schema.species2Animals) 

    def add(animal: Animal): Unit = {
      animals += animal
      animal.species += this
    }
  }

  class Habitat(val name: String) extends Entity[Habitat](Schema.habitats) {
    def keys: Product1[String] = Tuple1(name)

    lazy val animals = ownee(Schema.animals2Habitats)
  }

  class AnimalRepo extends SquerylRepo[Animal] {
    val table = Schema.animals
  }
}
