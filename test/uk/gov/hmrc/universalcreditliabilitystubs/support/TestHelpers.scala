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

import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.{InsertLiabilityRequest, TerminateLiabilityRequest}
import uk.gov.hmrc.universalcreditliabilitystubs.services.UcLiabilityService
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames

import scala.util.Random

trait TestHelpers {
  val service = new UcLiabilityService()

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
      HeaderNames.CorrelationId -> "3e8dae97-b586-4cef-8511-68ac12da9028",
      HeaderNames.OriginatorId  -> "gov-uk-originator-id"
    )

  val inValidHeaders: Seq[(String, String)] =
    Seq(
      HeaderNames.CorrelationId -> "3e8dae97-b586-4cef-8511-68ac12da9028"
    )

  val request: FakeRequest[JsValue] =
    FakeRequest().withBody(Json.toJson(validInsertLiabilityRequest)).withHeaders(validHeaders: _*)

  def generateNino(): String = {
    val number = f"${Random.nextInt(100000)}%06d"
    val nino   = s"AA$number"
    nino
  }

  def generateFakeRequest(requestBody: JsValue, headers: Seq[(String, String)]): FakeRequest[JsValue] =
    FakeRequest("POST", "/").withBody(Json.toJson(requestBody)).withHeaders(headers: _*)

  def getInsertLiabilityRequest: InsertLiabilityRequest = validInsertLiabilityRequest.as[InsertLiabilityRequest]

  def getTerminateLiabilityRequest: TerminateLiabilityRequest =
    validTerminateLiabilityRequest.as[TerminateLiabilityRequest]

}
