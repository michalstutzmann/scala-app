package com.github.mwegrz.app

import jline.console.ConsoleReader

/**
  * Hint: When running from SBT console in a forked process (`fork := true`) always set `connectInput in run := true`
  * so the CTRL-D signal listener works properly.
  */
trait ConsoleShutdown { this: StandaloneApp =>
  override private[app] def afterMain(): Unit = {
    do {
      Console.println("Press Ctrl-D to shut down")
    } while (new ConsoleReader().readLine() != null)
    removeShutdownHook()
    shutdown()
  }
}
