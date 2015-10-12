package org.chipmunk.entity.relation

import org.chipmunk.TestSchema
import org.chipmunk.entity.Entity
import org.chipmunk.test.InMemoryDb
import org.chipmunk.test.{ relation => test }
import org.chipmunk.util.Configurator
import org.scalatest.FlatSpec
import org.squeryl.PrimitiveTypeMode.transaction

trait RelationHandleBehaviors {
  self: FlatSpec with TestSchema with InMemoryDb =>

  def handleWithNonPersistedOwner[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E]): Unit = {

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
    handleOf: O => RelationHandle[E]): Unit = {

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
    contents: => Seq[E]): Unit = {

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
    handleOf: O => RelationHandle[E]): Unit = {

    it should "be persistent" in withTransaction {
      assert(handleOf(newOwner).state.isPersisted)
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

  def persistentOwnerHandle[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    handleOf: O => RelationHandle[E],
    toAdd: => E): Unit = {

    it should behave like persistentHandle(newOwner, handleOf)

    it should "add entities on += in transactions" in withTransaction {
      val owner = newOwner
      val handle = handleOf(owner)
      val addend = toAdd

      handle += addend
      assert(handle exists { _ == addend })
    }
  }

  def persistentOwneeHandle[O <: Entity[_], E <: Entity[_]](
    newOwner: => O,
    owneeHandleOf: O => RelationHandle[E],
    toAdd: => E,
    counterpartHandleOf: E => RelationHandle[O]): Unit = {

    it should behave like persistentHandle(newOwner, owneeHandleOf)

    it should "add entities on += in transactions" in withDb {
      val owner = newOwner
      val handle = owneeHandleOf(owner)

      val addend = toAdd
      val counterpartHandle = counterpartHandleOf(addend)

      transaction {
        counterpartHandle += owner
        handle += addend
      }

      transaction {
        /**
         * this check needs to be done in a separate transaction because the
         * isolation status is READ COMMITTED
         */
        assert(handle exists { _ == addend })
      }
    }
  }
}
