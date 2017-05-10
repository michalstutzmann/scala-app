package com.github.mwegrz.app

trait Shutdownable extends Runnable {
  override def run(): Unit = ()

  def shutdown(): Unit
}
