import sbt.*

object AppDependencies {

  private val bootstrapVersion = "10.5.0"

  val compile: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"                 % "2.13.0",
    "uk.gov.hmrc"   %% "bootstrap-backend-play-30" % bootstrapVersion
  )

  val test: Seq[ModuleID] = Seq(
    "io.github.wolfendale" %% "scalacheck-gen-regexp"  % "1.1.0",
    "org.scalatest"        %% "scalatest"              % "3.2.19",
    "org.scalatestplus"    %% "scalacheck-1-18"        % "3.2.19.0",
    "uk.gov.hmrc"          %% "bootstrap-test-play-30" % bootstrapVersion
  ).map(_ % Test)

  val it: Seq[ModuleID] = Seq(
    "com.atlassian.oai" % "swagger-request-validator-core" % "2.46.0"
  )
}
