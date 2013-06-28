package org.sisioh.dddbase.core.lifecycle.forwarding

import org.sisioh.dddbase.core.lifecycle.AsyncEntityWriter
import org.sisioh.dddbase.core.model.{Entity, Identity}
import scala.concurrent.{Future, ExecutionContext}

trait AsyncForwardingEntityWriter[R <: AsyncEntityWriter[_, ID, T], ID <: Identity[_], T <: Entity[ID]]
  extends AsyncEntityWriter[R, ID, T] {

  protected val delegateAsyncEntityWriter: AsyncEntityWriter[_, ID, T]

  protected def createInstance(state: Future[(AsyncEntityWriter[_, ID, T], Option[ID])]): Future[(R, Option[ID])]

  def store(entity: T)(implicit executor: ExecutionContext): Future[(R, ID)] = {
    val state = delegateAsyncEntityWriter.store(entity).map {
      result =>
        (result._1.asInstanceOf[AsyncEntityWriter[R, ID, T]], Some(result._2))
    }
    val instance = createInstance(state)
    instance.map {
      e =>
        (e._1, e._2.get)
    }
  }

  def delete(identity: ID)(implicit executor: ExecutionContext): Future[R] = {
    val state = delegateAsyncEntityWriter.delete(identity).map {
      result =>
        (result.asInstanceOf[AsyncEntityWriter[R, ID, T]], None)
    }
    val instance = createInstance(state)
    instance.map {
      e =>
        e._1
    }
  }

}
