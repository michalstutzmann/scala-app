import ReleaseTransformations._

val ScalaVersion = "2.12.3"
val CrossScalaVersions = Seq("2.11.11", ScalaVersion)
val ConfigVersion = "1.3.1"
val ScalaStructLogVersion = "0.1.2"
val JlineVersion = "2.12"
val AkkaVersion = "2.5.3"

lazy val root = (project in file("."))
  .enablePlugins(ReleasePlugin, ScalafmtPlugin)
  .settings(
    name := "scala-app",
    organization := "com.github.mwegrz",
    scalaVersion := ScalaVersion,
    crossScalaVersions := CrossScalaVersions,
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % ConfigVersion,
      "com.github.mwegrz" %% "scala-structlog" % ScalaStructLogVersion,
      "jline" % "jline" % JlineVersion,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion % Optional
    ),
    scalafmtOnCompile := true,
    // Release settings
    releaseTagName := { (version in ThisBuild).value },
    releaseTagComment := s"Release version ${(version in ThisBuild).value}",
    releaseCommitMessage := s"Set version to ${(version in ThisBuild).value}",
    releaseCrossBuild := true,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepCommandAndRemaining("publishSigned"),
      setNextVersion,
      commitNextVersion,
      releaseStepCommandAndRemaining("sonatypeReleaseAll"),
      pushChanges
    ),
    useGpg := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    // Publish settings
    crossPaths := true,
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
    licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html")),
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
