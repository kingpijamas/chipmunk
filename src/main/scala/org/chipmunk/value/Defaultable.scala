package org.chipmunk.value

import java.sql.{ Date => SQLDate }
import java.math.{ BigDecimal => JBigDecimal }
import java.sql.Timestamp
import java.util.UUID
import scala.annotation.implicitNotFound
import java.math.{BigDecimal => JBigDecimal}
import java.sql.{Date => SQLDate}

@implicitNotFound("No member of typeclass Defaultable in scope for ${T}")
trait Defaultable[T] {
  def defaultVal: T
}

object Defaultable {
  def defaultOf[T: Defaultable]: T =
    implicitly[Defaultable[T]].defaultVal

  implicit object DefaultableInt extends Defaultable[Int] {
    val defaultVal: Int = 0
  }

  implicit object DefaultableLong extends Defaultable[Long] {
    val defaultVal: Long = 0
  }

  implicit object DefaultableFloat extends Defaultable[Float] {
    val defaultVal: Float = 0.0f
  }

  implicit object DefaultableDouble extends Defaultable[Double] {
    val defaultVal: Double = 0.0
  }

  implicit object DefaultableBigDecimal extends Defaultable[JBigDecimal] {
    val defaultVal: JBigDecimal = new JBigDecimal(0)
  }

  implicit object DefaultableString extends Defaultable[String] {
    val defaultVal: String = ""
  }

  implicit object DefaultableDate extends Defaultable[SQLDate] {
    val defaultVal: SQLDate = new SQLDate(0)
  }

  implicit object DefaultableTimestamp extends Defaultable[Timestamp] {
    val defaultVal: Timestamp = new Timestamp(0)
  }

  implicit object DefaultableByteArray extends Defaultable[Array[Byte]] {
    val defaultVal: Array[Byte] = Array[Byte]()
  }

  implicit object DefaultableBoolean extends Defaultable[Boolean] {
    val defaultVal: Boolean = false
  }

  implicit object DefaultableUUID extends Defaultable[UUID] {
    val defaultVal: UUID = new UUID(0, 0)
  }

  implicit object DefaultableType extends Defaultable[Type] {
    val defaultVal: Type = new Type("")
  }

  implicit object DefaultableDuration extends Defaultable[Duration] {
    val defaultVal: Duration = new Duration
  }

  implicit def defaultableOption[T: Defaultable]: Defaultable[Option[T]] =
    new Defaultable[Option[T]] {
      val defaultVal: Option[T] = Some(defaultOf[T])
    }
}
