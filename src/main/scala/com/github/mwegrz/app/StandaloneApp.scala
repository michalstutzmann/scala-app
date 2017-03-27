package com.github.mwegrz.app

import java.util.concurrent.{ TimeUnit, TimeoutException }
import com.typesafe.config.{ Config, ConfigFactory }
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.concurrent.duration.{ Duration, FiniteDuration, MILLISECONDS }
import scala.sys.ShutdownHookThread
import scala.util.{ Failure, Success, Try }
import com.github.mwegrz.scalastructlog.Logging

abstract class StandaloneApp(val config: Config = ConfigFactory.load())(implicit ec: ExecutionContext) extends Logging {
  private val initTimeout: Duration = FiniteDuration(config.getDuration("standalone-app.init-timeout", TimeUnit.MILLISECONDS), MILLISECONDS)

  private val shutdownTimeout: Duration = FiniteDuration(config.getDuration("standalone-app.shutdown-timeout", TimeUnit.MILLISECONDS), MILLISECONDS)

  private var shutdownable: Try[Shutdownable] = _

  private var hook: ShutdownHookThread = _

  final def main(args: Array[String]): Unit = {
    assert(shutdownable == null)
    log.debug("Initializing")
    val initialization: Future[Try[Shutdownable]] = Future(Try(init(args)))
    shutdownable = Try(Await.result(initialization, initTimeout)).flatten
    shutdownable match {
      case Success(r) =>
        log.debug("Initialized")
        addShutdownHook()
        Future(afterMain())
        log.debug("Running")
        r.run()
        log.debug("Finished")
      case Failure(t: TimeoutException) =>
        log.error(s"Timed out while waiting for the initialization to complete. Terminating the JVM", t)
        System.exit(1)
      case Failure(t) =>
        log.error("Initialization failed", t)
        throw t
    }
  }

  final def shutdown(): Unit = {
    assert(shutdownable != null)
    log.debug("Shutting down")
    shutdownable foreach { s =>
      val shutdown = s.shutdown()
      Try(Await.result(shutdown, initTimeout)) match {
        case Success(e) => log.info("Shut down")
        case Failure(t: TimeoutException) =>
          log.error(s"Timed out while waiting for the shutdown to complete. Terminating the JVM", t)
          System.exit(1)
        case Failure(t) =>
          log.error("Shutdown failed", t)
          throw t
      }
    }
  }

  def init(args: Array[String]): Shutdownable

  private[app] def afterMain(): Unit = ()

  private[app] final def addShutdownHook(): Unit = hook = sys.addShutdownHook(shutdown())

  private[app] final def removeShutdownHook(): Unit = hook.remove()
}
