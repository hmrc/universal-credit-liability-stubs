import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.3.5"

lazy val microservice = Project("universal-credit-liability-stubs", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
    // suppress warnings in generated routes files
    scalacOptions += "-Wconf:src=routes/.*:s",
    Test / unmanagedResourceDirectories += baseDirectory.value / "it" / "resources"
  )
  .settings(CodeCoverageSettings.settings*)
  .settings(PlayKeys.playDefaultPort := 16108)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(
    libraryDependencies ++= AppDependencies.it,
    // dependencyOverrides for "swagger-request-validator-core" % "2.44.8"
    // Scala module 2.14.3 requires Jackson Databind version >= 2.14.0 and < 2.15.0
    dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3"
  )
