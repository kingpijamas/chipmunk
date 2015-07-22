package org.chipmunk

trait Keyed extends Equals {
  protected def keys: Product

  def canEqual(other: Any): Boolean = other.isInstanceOf[Keyed]

  override def equals(other: Any): Boolean = other match {
    case that: Keyed => that.canEqual(this) && keysAreEqual(that)
    case _                => false
  }

  private[this] def keysAreEqual(other: Keyed): Boolean =
    keys == other.keys

  override def hashCode: Int = keys.hashCode
}
