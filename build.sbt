import ReleaseTransformations._

val ScalaVersion = "2.13.1"
val ConfigVersion = "1.3.4"
val ScalaStructLogVersion = "0.1.15"
val JlineVersion = "2.12"
val AkkaVersion = "2.5.25"

lazy val root = (project in file("."))
  .enablePlugins(ReleasePlugin, ScalafmtPlugin)
  .settings(
    name := "scala-app",
    organization := "com.github.mwegrz",
    scalaVersion := ScalaVersion,
    crossScalaVersions := Seq(scalaVersion.value, "2.12.10"),
    scalacOptions ++=
      (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, n)) if n >= 13 => Seq("-Xsource:2.14")
        case _ => Seq("-Yno-adapted-args", "-deprecation")
      }),
    resolvers += "Sonatype Maven Snapshots" at "https://oss.sonatype.org/content/repositories/releases",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % ConfigVersion,
      "com.github.mwegrz" %% "scala-structlog" % ScalaStructLogVersion,
      "jline" % "jline" % JlineVersion % Optional,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion % Optional,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion % Optional
    ),
    scalafmtOnCompile := true,
    // Release settings
    releaseCrossBuild := true,
    releaseTagName := { (version in ThisBuild).value },
    releaseTagComment := s"Release version ${(version in ThisBuild).value}",
    releaseCommitMessage := s"Set version to ${(version in ThisBuild).value}",
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("+publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommandAndRemaining("sonatypeReleaseAll"),
      pushChanges
    ),
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    // Publish settings
    autoScalaLibrary := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ =>
      false
    },
    licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage := Some(url("http://github.com/mwegrz/scala-app")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/mwegrz/scala-app.git"),
        "scm:git@github.com:mwegrz/scala-app.git"
      )
    ),
    developers := List(
      Developer(
        id = "mwegrz",
        name = "Michał Węgrzyn",
        email = null,
        url = url("http://github.com/mwegrz")
      )
    )
  )
