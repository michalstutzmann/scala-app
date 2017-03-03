package com.github.mwegrz.app

import com.typesafe.config.{ Config, ConfigFactory }

trait Factory[A] {
  protected def configPath: String = getClass
    .getSimpleName
    .stripSuffix("$")
    .replaceAll("([A-Z])", " $0")
    .split(" ")
    .drop(1)
    .map(_.toLowerCase)
    .mkString("-")

  def apply(config: Config): A = apply(config, configPath)

  def apply(config: Config, path: String): A = forConfig(config.getConfig(path)
    .withFallback(ConfigFactory.defaultReference.getConfig(configPath)))

  def forConfig(config: Config): A
}
