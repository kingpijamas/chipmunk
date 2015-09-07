package org.chipmunk.schema

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import org.chipmunk.schema.ForeignKeys.mandatoryFk

object ForeignKeys {
  /**
   * @see <a href="http://stackoverflow.com/a/27351138"/>
   */
  def mandatoryFk[T: c.WeakTypeTag]
    (c: blackbox.Context)
    (value: c.Expr[T])
  : c.Tree = {
    import c.universe._

    val symbol: TermSymbol = value.tree.symbol.asTerm
    val tpe = value.tree.tpe

    q"""
      {
        import org.chipmunk.schema.ForeignKey
        new ForeignKey[$tpe] {
          def value: $tpe = $value
  
          def set(x: $tpe): Unit = {
            $value = x
          }
        }
      }
    """
  }
}

trait ForeignKeys {
  def &[T](value: T): ForeignKey[T] = macro mandatoryFk[T]
}
