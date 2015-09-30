package org.chipmunk.entity.relation

import org.chipmunk.DbSpec
import org.chipmunk.entity.Entity
import org.scalatest.FlatSpec
import org.chipmunk.test.{ relation => test }
import org.chipmunk.test.InMemoryDb
import org.chipmunk.TestSchema
import org.scalatest.Suite

trait RelationHandleBehaviors {
  self: FlatSpec with TestSchema with InMemoryDb =>

  def handleWithNonPersistedOwner[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E]) {

    it should "have a transient owner" in {
      assert(!newOwner.isPersisted)
    }

    it should "not fail on creation outside transactions" in {
      handleOf(newOwner)
    }

    it should "be in transient state on creation outside transactions" in {
      assert(handleOf(newOwner).state.isTransient)
    }
  }

  def handleWithPersistedOwner[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E]) {

    it should "have a persistent owner" in withTransaction {
      assert(newOwner.isPersisted)
    }

    it should "be in persistent state on creation in transactions" in
      withTransaction {
        assert(handleOf(newOwner).state.isPersisted)
      }
  }

  def transientHandle[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E],
    toAdd: => E,
    contents: => Seq[E]) {

    it should "be transient" in {
      assert(handleOf(newOwner).state.isTransient)
    }

    it should "add entities on += outside transactions" in {
      val handle = handleOf(newOwner)
      val addend = toAdd

      handle += addend
      assert(handle exists { _ == addend })
    }

    it should "be empty on clear outside transactions" in {
      val handle = handleOf(newOwner)
      handle.clear() //FIXME it'd be nice to have it have something in it before this
      assert(handle.isEmpty)
    }

    it should "return a test relation on toSqueryl" in {
      assert(handleOf(newOwner).toSqueryl.isInstanceOf[test.Query[_]])
    }

    it should "not change its contents when iterated" in {
      val handle = handleOf(newOwner)
      val auxHandle = handleOf(newOwner)
      val contentsAfterIteration = auxHandle filter { _ => true }
      assert(contentsAfterIteration == contents)
    }

    it should "persist its contents on persist when owner's body is persisted" in
      withTransaction {
        val owner = newOwner
        owner.persistBody()
        val handle = handleOf(owner)
        handle.persist()
        assert(handle forall { _.isPersisted })
      }
  }

  def persistentHandle[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E],
    toAdd: => E) {

    it should "be persistent" in withTransaction {
      assert(handleOf(newOwner).state.isPersisted)
    }

    it should "add entities on += in transactions" in withTransaction {
      val owner = newOwner
      val handle = handleOf(owner)
      val addend = toAdd

      handle += addend
      assert(handle exists { _ == addend })
    }

    it should "be empty on clear in transactions" in withTransaction {
      val handle = handleOf(newOwner)
      handle.clear() //FIXME it'd be nice to have it have something in it before this
      assert(handle.isEmpty)
    }

    it should "not return a test relation on toSqueryl" in withDb {
      assert(!handleOf(newOwner).toSqueryl.isInstanceOf[test.Query[_]])
    }
  }
}
