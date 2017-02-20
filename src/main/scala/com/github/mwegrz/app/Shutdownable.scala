package com.github.mwegrz.app

import scala.concurrent.Future

trait Shutdownable extends Runnable {
  override def run(): Unit = ()

  def shutdown(): Future[Unit]
}
