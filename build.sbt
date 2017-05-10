import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences.{DoubleIndentClassDeclaration, PlaceScaladocAsterisksBeneathSecondAsterisk}

val ScalaVersion = "2.12.2"
val CrossScalaVersions = Seq("2.11.11", ScalaVersion)
val ConfigVersion = "1.3.1"
val ScalaStructLogVersion = "0.1.1-SNAPSHOT"
val JlineVersion = "2.12"

lazy val root = (project in file(".")).
  enablePlugins(GitBranchPrompt, ReleasePlugin, SbtScalariform).
  settings(
    name := "scala-app",
    organization := "com.github.mwegrz",
    scalaVersion := ScalaVersion,
    crossScalaVersions := CrossScalaVersions,
    resolvers += "Sonatype Maven Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % ConfigVersion,
      "com.github.mwegrz" %% "scala-structlog" % ScalaStructLogVersion,
      "jline" % "jline" % JlineVersion
    ),
    // Publishing
    publishMavenStyle := true,
    crossPaths := true,
    autoScalaLibrary := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <url>http://github.com/mwegrz/scala-app</url>
        <licenses>
          <license>
            <name>Apache License 2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:mwegrz/scala-app.git</url>
          <connection>scm:git:git@github.com:mwegrz/scala-app.git</connection>
        </scm>
        <developers>
          <developer>
            <id>mwegrz</id>
            <name>Michał Węgrzyn</name>
            <url>http://github.com/mwegrz</url>
          </developer>
        </developers>),
    releaseTagComment := s"Released ${(version in ThisBuild).value}",
    releaseCommitMessage := s"Set version to ${(version in ThisBuild).value}",
    ScalariformKeys.preferences := ScalariformKeys.preferences.value
      .setPreference(DoubleIndentClassDeclaration, true)
      .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
  )
