package org.chipmunk.util

object Validations {
  def assumeState(assumption: Boolean, text: => String): Unit = {
    if (!assumption) { throw new IllegalStateException(text) }
  }
}
