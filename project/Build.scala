import xerial.sbt.Sonatype.SonatypeKeys._
import sbt.Keys._
import sbt._

object DDDBaseBuild extends Build {
  val specs2 = "org.specs2" %% "specs2" % "2.3.12" % "test"
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.0" % "test"
  val junit = "junit" % "junit" % "4.8.1" % "test"
  val mockito = "org.mockito" % "mockito-core" % "1.9.5" % "test"

  lazy val commonSettings = Defaults.defaultSettings ++ Seq(
    sonatypeProfileName := "org.sisioh",
    organization := "org.sisioh",
    scalaVersion := "2.10.5",
    crossScalaVersions := Seq("2.10.5", "2.11.6"),
    scalacOptions ++= Seq("-feature", "-unchecked", "-deprecation"),
    shellPrompt := {
      "sbt (%s)> " format projectId(_)
    },
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := {
      _ => false
    },
    pomExtra := (
      <url>https://github.com/sisioh/sisioh-dddbase</url>
        <licenses>
          <license>
            <name>Apache License Version 2.0</name>
            <url>http://www.apache.org/licenses/</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:sisioh/scala-dddbase.git</url>
          <connection>scm:git:git@github.com:sisioh/scala-dddbase.git</connection>
        </scm>
        <developers>
          <developer>
            <id>j5ik2o</id>
            <name>Junichi Kato</name>
            <url>http://j5ik2o.me</url>
          </developer>
        </developers>
      ),
    credentials := {
      val ivyCredentials = (baseDirectory in LocalRootProject).value / ".credentials"
      Credentials(ivyCredentials) :: Nil
    }
  )

  val root = Project(
    id = "scala-dddbase",
    base = file("."),
    settings = commonSettings,
    aggregate = Seq(core, forwarding, memory, spec)
  )

  val core = Project(
    id = "scala-dddbase-core",
    base = file("scala-dddbase-core"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(mockito, specs2)
    )
  )

  val forwarding: Project = Project(
    id = "scala-dddbase-lifecycle-repositories-forwarding",
    base = file("scala-dddbase-lifecycle-repositories-forwarding"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(mockito, specs2)
    )
  ) dependsOn (core)

  val memory: Project = Project(
    id = "scala-dddbase-lifecycle-repositories-memory",
    base = file("scala-dddbase-lifecycle-repositories-memory"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(mockito, specs2)
    )
  ) dependsOn (core, forwarding)


  val spec = Project(
    id = "scala-dddbase-spec",
    base = file("scala-dddbase-spec"),
    settings = commonSettings ++ Seq(
      libraryDependencies ++= Seq(junit, scalaTest, mockito, specs2)
    )
  ) dependsOn (core)

  def projectId(state: State) = extracted(state).currentProject.id

  def extracted(state: State) = Project extract state

}
