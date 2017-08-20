package com.github.mwegrz.app

import akka.actor.SupervisorStrategy.{ Decider, Escalate, Restart }
import akka.actor.{
  ActorInitializationException,
  ActorKilledException,
  ActorSystem,
  DeathPactException,
  OneForOneStrategy,
  Props,
  SupervisorStrategyConfigurator
}
import com.github.mwegrz.scalastructlog.Logging
import com.typesafe.config.{ Config, ConfigFactory }

import scala.concurrent.{ Await, ExecutionContext }
import scala.concurrent.duration._

abstract class ActorSystemApp(override val config: Config = ConfigFactory.load())(
    implicit executionContext: ExecutionContext = ExecutionContext.Implicits.global)
    extends StandaloneApp(config) { app =>
  override final def init(args: Array[String]): Shutdownable = new Shutdownable {

    private val actorSystem = ActorSystem("main", config)

    override def run(): Unit = actorSystem.actorOf(props(args), "main")

    override def shutdown(): Unit = Await.ready(actorSystem.terminate(), Duration.Inf)
  }

  protected def props(args: Array[String]): Props
}

final class SupervisorStrategy extends SupervisorStrategyConfigurator with Logging {
  override def create(): akka.actor.SupervisorStrategy = {
    def decider: Decider = {
      case _: ActorInitializationException => Escalate
      case _: ActorKilledException => Escalate
      case _: DeathPactException => Escalate
      case _: Exception => Restart
    }
    val strategy = OneForOneStrategy()(decider)
    log.debug("Initialized")
    strategy
  }
}
