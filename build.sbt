name := "jobcoin-mixer"

version := "0.1"

scalaVersion := "2.12.6"

trapExit := false

scalacOptions ++= Seq("-deprecation", "-feature")

lazy val root = (project in file(".")).
  settings(
    name := "jobcoin-mixer",
    version := "1.0",
    scalaVersion := "2.12.6",
    mainClass in Compile := Some("com.gemini.jobcoin.JobcoinServer")
  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.0-M1"
libraryDependencies += "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.0-M1"
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.7"
libraryDependencies += "com.github.swagger-akka-http" %% "swagger-akka-http" % "1.0.0"
libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.3.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test,
  "com.typesafe.akka" %% "akka-http"   % "10.1.3",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3"
)

homepage := Some(url("https://github.com/codologist/jobcoin-mixer"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

mainClass in (Compile, run) := Some("JobcoinServer")