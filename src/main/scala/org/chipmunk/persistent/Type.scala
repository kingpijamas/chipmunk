package org.chipmunk.persistent

import org.squeryl.customtypes.StringField

//TODO: couldn't this be type safe?
class Type(val className: String) extends StringField(className) {
  def this(clazz: Class[_]) = this(clazz.getName)

  def asClass: Class[_] = Class.forName(className)

  def classNameProper: String =
    if (className endsWith "$") className dropRight 1 else className
}