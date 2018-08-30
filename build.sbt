name := "jobcoin-mixer"

version := "0.1"

scalaVersion := "2.12.6"

trapExit := false

scalacOptions ++= Seq("-deprecation", "-feature")

//lazy val root = (project in file(".")).
//  settings(
//    name := "jobcoin-mixer",
//    version := "1.0",
//    scalaVersion := "2.12.6",
//    mainClass in Compile := Some("com.gemini.jobcoin.JobcoinServer")
//  )

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.7"
libraryDependencies += "com.github.swagger-akka-http" %% "swagger-akka-http" % "1.0.0"
libraryDependencies += "io.swagger" % "swagger-jaxrs" % "1.5.20"
libraryDependencies += "ch.megard" %% "akka-http-cors" % "0.3.0"
libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.3.0"
libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.25"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.14",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.14",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.14" % Test,
  "com.typesafe.akka" %% "akka-http"   % "10.1.4",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.4"
)