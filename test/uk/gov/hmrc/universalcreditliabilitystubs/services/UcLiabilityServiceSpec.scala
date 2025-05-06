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

package uk.gov.hmrc.universalcreditliabilitystubs.services

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Results.BadRequest
import play.api.test.FakeRequest
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.{Failure, Failures}
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.{SubmitLiabilityRequest, UniversalCreditLiabilityDetail}

import java.time.LocalDate

class UcLiabilityServiceSpec extends AnyWordSpec with Matchers {

  val service = new UcLiabilityService()

  val validSubmitLiabilityRequest: JsValue =
    Json.parse("""
                 |{
                 |  "universalCreditLiabilityDetail": {
                 |    "universalCreditRecordType": "LCW/LCWRA",
                 |    "universalCreditAction": "Insert",
                 |    "dateOfBirth": "2002-10-10",
                 |    "liabilityStartDate": "2015-08-19",
                 |    "liabilityEndDate": "2025-01-04"
                 |  }
                 |}
                 |""".stripMargin)

  val invalidSubmitLiabilityRequest: JsValue =
    Json.parse("""
                 |{
                 |  "universalCreditLiabilityDetail": {
                 |    "universalCreditRecordType": "LCW/LCWRA",
                 |    "universalCreditAction": "Insert",
                 |    "dateOfBirth": "2002-10-10",
                 |    "liabilityEndDate": "2025-01-04"
                 |  }
                 |}
                 |""".stripMargin)

  val validHeaders: Seq[(String, String)] =
    Seq(
      "correlationId" -> "3e8dae97-b586-4cef-8511-68ac12da9028"
    )

  "validateRequest" must {

    "return a SubmitLiabilityRequest object given an valid request body" in {

      val validHeaders: Seq[(String, String)] = Seq(
        "correlationId" -> "3e8dae97-b586-4cef-8511-68ac12da9028"
      )

      val request: FakeRequest[JsValue] =
        FakeRequest("POST", "/").withBody(Json.toJson(validSubmitLiabilityRequest)).withHeaders(validHeaders: _*)
      val result                        = service.validateRequest(request, "AA123456")

      result mustBe Right(
        SubmitLiabilityRequest(
          universalCreditLiabilityDetail = UniversalCreditLiabilityDetail(
            universalCreditRecordType = "LCW/LCWRA",
            universalCreditAction = "Insert",
            dateOfBirth = LocalDate.of(2002, 10, 10),
            liabilityStartDate = LocalDate.of(2015, 8, 19),
            liabilityEndDate = Some(LocalDate.of(2025, 1, 4))
          )
        )
      )
    }

    "return a BadRequest Result given an invalid nino" in {

      val validHeaders: Seq[(String, String)] = Seq(
        "correlationId" -> "3e8dae97-b586-4cef-8511-68ac12da9028"
      )

      val request: FakeRequest[JsValue] =
        FakeRequest().withBody(Json.toJson(validSubmitLiabilityRequest)).withHeaders(validHeaders: _*)
      val result                        = service.validateRequest(request, "nino")

      result mustBe Left(
        BadRequest(
          toJson(
            Failures(
              failures =
                Seq(Failure(reason = "Constraint Violation - Invalid/Missing input parameter: nino", code = "400.1"))
            )
          )
        )
      )
    }

    "return a BadRequest Result given an invalid correlationId" in {

      val validHeaders: Seq[(String, String)] = Seq(
        "correlationId" -> "3e8dae97-b586-4cef-8511"
      )

      val request: FakeRequest[JsValue] =
        FakeRequest().withBody(Json.toJson(validSubmitLiabilityRequest)).withHeaders(validHeaders: _*)
      val result                        = service.validateRequest(request, "AA123456")

      result mustBe Left(
        BadRequest(
          toJson(
            Failures(
              failures = Seq(
                Failure(
                  reason = "Constraint Violation - Invalid/Missing input parameter: correlationId",
                  code = "400.1"
                )
              )
            )
          )
        )
      )
    }

    "return a BadRequest Result given an invalid request body" in {

      val validHeaders: Seq[(String, String)] = Seq(
        "correlationId" -> "3e8dae97-b586-4cef-8511-68ac12da9028"
      )

      val request: FakeRequest[JsValue] =
        FakeRequest("POST", "/").withBody(Json.toJson(invalidSubmitLiabilityRequest)).withHeaders(validHeaders: _*)
      val result                        = service.validateRequest(request, "AA123456")

      result mustBe Left(
        BadRequest(
          Json.toJson(
            Failures(
              failures = Seq(
                Failure(
                  reason =
                    "Constraint Violation - Invalid/Missing input parameter: universalCreditLiabilityDetail/liabilityStartDate",
                  code = "400.1"
                )
              )
            )
          )
        )
      )
    }
  }
}
