package org.chipmunk.entity.relation

import org.chipmunk.DbSpec
import org.chipmunk.entity.Entity
import org.scalatest.FlatSpec
import org.chipmunk.test.Transactions
import org.chipmunk.test.{ relation => test }
import org.chipmunk.test.InMemoryDb
import org.chipmunk.TestSchema

trait RelationHandleBehaviors[O <: Entity[_], E <: Entity[_]] {
  this: FlatSpec with TestSchema with Transactions with InMemoryDb =>

  def handleWithNonPersistedOwner(
    newHandle: => RelationHandle[E],
    owner: => O) {
    assume(!owner.isPersisted)

    it should "not fail on creation outside transactions" in { newHandle }

    it should "be in transient state on creation outside transactions" in {
      assert(newHandle.state.isTransient)
    }
  }

  def handleWithPersistedOwner(newHandle: => RelationHandle[E], owner: O) {
    assume(owner.isPersisted)

    it should "be in persistent state on creation in transactions" in {
      assert(!newHandle.state.isTransient)
    }
  }

  def transientHandle(
    newHandle: => RelationHandle[E],
    owner: => O,
    toAdd: => E) {
    assume(newHandle.state.isTransient)

    it should "add entities on += outside transactions" in {
      val handle = newHandle
      handle += toAdd
      assert(handle exists { _ == toAdd })
    }

    it should "be empty on clear outside transactions" in {
      val handle = newHandle
      handle.clear()
      assert(handle.isEmpty)
    }

    it should "return a test relation on toSqueryl" in {
      assert(newHandle.toSqueryl.isInstanceOf[test.Query[_]])
    }
  }

  def nonEmptyTransientHandle(
    newHandle: => RelationHandle[E],
    owner: => O,
    contents: => Seq[E]) {
    assume(!newHandle.isEmpty)

    it should "not change its contents when iterated" in {
      val handle = newHandle
      val contentsAfterIteration = newHandle filter { _ => true }
      assert(contentsAfterIteration == contents)
    }

    it should "persist its contents on persist when owner's body is persisted" in {
      owner.persistBody()
      val handle = newHandle
      handle.persist()
      assert(handle forall { _.isPersisted })
    }
  }

  def persistentHandle(newHandle: => RelationHandle[E], entity: E) {
    assume(!newHandle.state.isTransient)

    it should "add entities on += in transactions" in withTransaction {
      val handle = newHandle
      handle += entity
      assert(handle exists { _ == entity })
    }

    it should "be empty on clear in transactions" in withTransaction {
      val handle = newHandle
      handle.clear()
      assert(handle.isEmpty)
    }

    it should "not return a test relation on toSqueryl" in {
      assert(!newHandle.toSqueryl.isInstanceOf[test.Query[_]])
    }
  }
}
