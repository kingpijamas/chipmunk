package org.chipmunk.persistent.relation

import org.chipmunk.persistent.Entity
import org.squeryl.dsl.{ ManyToOne => SManyToOne }

trait ManyToOne[O <: Entity[_]] extends Relation[O, SManyToOne[O]]
