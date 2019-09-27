package com.github.mwegrz.app

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.ExecutionContext

abstract class TypedActorSystemApp(override val config: Config = ConfigFactory.load())(
    implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global
) extends StandaloneApp(config) { app =>
  override final def init(args: Array[String]): Shutdownable = new Shutdownable {

    private var actorSystem: ActorSystem[Unit] = _

    override def run(): Unit = actorSystem = ActorSystem(createMainBehavior(args), "main", config)

    override def shutdown(): Unit = if (actorSystem != null) actorSystem.terminate()
  }

  protected def createMainBehavior(args: Array[String]): Behavior[Unit]
}
