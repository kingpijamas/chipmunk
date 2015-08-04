package org.chipmunk.types

import org.squeryl.customtypes.StringField

//TODO: couldn't this be type safe?
class Type(val classNameProper: String) extends StringField(classNameProper) {
  def this(clazz: Class[_]) = this(clazz.getName)

  def asClass: Class[_] = Class.forName(classNameProper)

  def className: String =
    if (classNameProper endsWith "$") classNameProper dropRight 1 else classNameProper
}
