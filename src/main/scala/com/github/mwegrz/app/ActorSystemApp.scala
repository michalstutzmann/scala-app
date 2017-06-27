package com.github.mwegrz.app

import akka.actor.{ ActorSystem, Props }
import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration.Duration

abstract class ActorSystemApp(implicit executionContext: ExecutionContext) extends StandaloneApp {
  override final def init(args: Array[String]): Shutdownable = new Shutdownable {
    private val actorSystem = ActorSystem("main", config)

    override def run(): Unit = actorSystem.actorOf(props(args), "main")

    override def shutdown(): Unit = Await.ready(actorSystem.terminate(), Duration.Inf)
  }

  protected def props(args: Array[String]): Props
}
