/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.universalcreditliabilitystubs.support

import org.scalacheck.Gen
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.UniversalCreditRecordType
import uk.gov.hmrc.universalcreditliabilitystubs.services.{MappingService, SchemaValidationService}
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ValidationPatterns.DatePattern
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames
import wolfendale.scalacheck.regexp.RegexpGen

import scala.util.Random
import scala.util.matching.Regex

trait TestHelpers {

  val validDateGen: Gen[String] = RegexpGen.from(DatePattern.toString())

  val dateLikeString: Gen[String] = RegexpGen.from("(19|20)[0-9]{2}[-][0-9]{2}[-][0-9]{2}")

  val randomStrings: Gen[String] = Gen.oneOf(
    "pjnc8d73pl",
    "3xfryd9o34",
    "63grmq7skl",
    "w3m3715fjh",
    "1w0o30nwie"
  )

  val mixedDateGen: Gen[String] = Gen.frequency(
    (8, validDateGen),
    (2, dateLikeString),
    (4, randomStrings)
  )

  val ucRecordTypeGen: Gen[UniversalCreditRecordType] =
    Gen.oneOf(UniversalCreditRecordType.UC, UniversalCreditRecordType.LCW_LCWRA)

  val schemaValidationService = new SchemaValidationService()

  val mappingService = new MappingService()

  val validInsertLiabilityRequest: JsValue =
    Json.parse("""
        |{
        |  "universalCreditLiabilityDetails": {
        |    "universalCreditRecordType": "LCW/LCWRA",
        |    "dateOfBirth": "2002-10-10",
        |    "liabilityStartDate": "2015-08-19",
        |    "liabilityEndDate": "2025-01-04"
        |  }
        |}
        |""".stripMargin)

  val invalidInsertLiabilityRequest: JsValue =
    Json.parse("""
        |{
        |  "universalCreditLiabilityDetails": {
        |    "universalCreditRecordType": "LCW/LCWRA",
        |    "dateOfBirth": "2002-10-10",
        |    "liabilityEndDate": "2025-01-04"
        |  }
        |}
        |""".stripMargin)

  val validTerminateLiabilityRequest: JsValue =
    Json.parse("""
        |{
        |  "ucLiabilityTerminationDetails": {
        |    "universalCreditRecordType": "LCW/LCWRA",
        |    "liabilityStartDate": "2015-08-19",
        |    "liabilityEndDate": "2025-01-04"
        |  }
        |}
        |""".stripMargin)

  val inValidTerminateLiabilityRequest: JsValue =
    Json.parse("""
        |{
        |  "ucLiabilityTerminationDetails": {
        |    "universalCreditRecordType": "LCW/LCWRA",
        |    "liabilityStartDate": "2015-08-19"
        |  }
        |}
        |""".stripMargin)

  val validHeaders: Seq[(String, String)] =
    Seq(
      HeaderNames.Authorization     -> "Basic bG9jYWwtY2xpZW50LWlkOmxvY2FsLWNsaWVudC1zZWNyZXQ=", // Base64 for local-client-id:local-client-secret
      HeaderNames.CorrelationId     -> "3e8dae97-b586-4cef-8511-68ac12da9028",
      HeaderNames.GovUkOriginatorId -> "gov-uk-originator-id"
    )

  val missingAuthorizationHeader: Seq[(String, String)] =
    Seq(
      HeaderNames.CorrelationId     -> "3e8dae97-b586-4cef-8511-68ac12da9028",
      HeaderNames.GovUkOriginatorId -> "gov-uk-originator-id"
    )

  val missingOriginatorIdHeader: Seq[(String, String)] =
    Seq(
      HeaderNames.Authorization -> "Basic bG9jYWwtY2xpZW50LWlkOmxvY2FsLWNsaWVudC1zZWNyZXQ=",
      HeaderNames.CorrelationId -> "3e8dae97-b586-4cef-8511-68ac12da9028"
    )

  val missingCorrelationIdHeader: Seq[(String, String)] =
    Seq(
      HeaderNames.Authorization     -> "Basic bG9jYWwtY2xpZW50LWlkOmxvY2FsLWNsaWVudC1zZWNyZXQ=",
      HeaderNames.GovUkOriginatorId -> "gov-uk-originator-id"
    )

  def generateNino(): String = {
    val number = f"${Random.nextInt(1000)}%03d"
    val nino   = s"KZ000$number"
    nino
  }

  def generateNinoWithPrefix(prefix: String): String = {
    val prefixPattern: Regex = "^[A-Za-z]{2}[0-9]{3}$".r

    require(
      prefixPattern.matches(prefix),
      s"Invalid prefix provided '$prefix'. Prefix must be 2 letters followed by 3 digits."
    )

    val number = f"${Random.nextInt(1000)}%03d"
    val nino   = s"$prefix$number"
    nino
  }

  def generateFakeRequest(requestBody: JsValue, headers: Seq[(String, String)]): FakeRequest[JsValue] =
    FakeRequest("POST", "/").withBody(Json.toJson(requestBody)).withHeaders(headers: _*)

}
