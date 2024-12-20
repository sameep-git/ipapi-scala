ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

lazy val root = (project in file("."))
  .settings(
    name := "ipapi-scala",
    idePackagePrefix := Some("com.ipquery.ipapi")
  )

libraryDependencies += "com.softwaremill.sttp.client4" %% "core" % "4.0.0-M20"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.10",
  "io.circe" %% "circe-generic" % "0.14.10",
  "io.circe" %% "circe-parser" % "0.14.10"
)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % Test