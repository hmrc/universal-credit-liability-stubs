import uk.gov.hmrc.DefaultBuildSettings

ThisBuild / scalaVersion := "3.3.7"
ThisBuild / majorVersion := 0
ThisBuild / semanticdbEnabled := true

lazy val microservice = Project("universal-credit-liability-stubs", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    PlayKeys.playDefaultPort := 16108,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    CodeCoverageSettings.settings,
    scalacOptions += "-Wconf:src=routes/.*:s", // suppress warnings in generated routes files
    Test / unmanagedResourceDirectories += baseDirectory.value / "it" / "resources"
  )

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(
    DefaultBuildSettings.itSettings(),
    libraryDependencies ++= AppDependencies.it,
    // dependencyOverrides for "swagger-request-validator-core" % "2.44.8"
    // Scala module 2.15.3 requires Jackson Databind version >= 2.15.0 and < 2.16.0
    dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.4"
  )

addCommandAlias("prePrChecks", "; scalafmtCheckAll; scalafmtSbtCheck; scalafixAll --check")
addCommandAlias("checkCodeCoverage", "; clean; coverage; test; it/test; coverageReport")
addCommandAlias("lintCode", "; scalafmtAll; scalafmtSbt; scalafixAll")
