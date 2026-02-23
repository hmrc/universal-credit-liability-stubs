/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.universalcreditliabilitystubs.controllers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers.*
import uk.gov.hmrc.universalcreditliabilitystubs.config.AppConfig
import uk.gov.hmrc.universalcreditliabilitystubs.controllers.UcLiabilityController
import uk.gov.hmrc.universalcreditliabilitystubs.services.{MappingService, SchemaValidationService}
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames.GovUkOriginatorId

import scala.concurrent.Future

class UcLiabilityControllerSpec extends AnyWordSpec with Matchers with TestHelpers with ScalaFutures {

  private val mockSchemaValidationService = mock[SchemaValidationService]
  private val mockMappingService          = mock[MappingService]
  private val mockAppConfig               = mock[AppConfig]

  private val testUcLiabilityController = new UcLiabilityController(
    stubControllerComponents(),
    schemaValidationService = mockSchemaValidationService,
    mappingService = mockMappingService,
    appConfig = mockAppConfig
  )

  private def assertForbidden(result: Either[Result, _]): Unit = {
    val actualResult = extractLeftOrFail(result)

    actualResult.header.status mustBe FORBIDDEN

    val body = contentAsJson(Future.successful(actualResult))
    (body \ "code").as[String] mustBe ApplicationConstants.ErrorCodes.ForbiddenCode
    (body \ "reason").as[String] mustBe ApplicationConstants.ForbiddenReason
  }

  "UcLiabilityNotificationController" must {

    "return right" when {
      "given a valid originatorId" in {
        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> ("A" * 3)))
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        result mustBe Right("A" * 3)
      }

      "given a valid GovUkOriginatorId for Special characters: '{}, [], (), @, !, *, -, ?'" in {
        val request = generateFakeRequest(
          requestBody = Json.obj(),
          headers = Seq(GovUkOriginatorId -> "{[(V@l!d-0r!g!n4t*r-1D?)]}")
        )
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        result mustBe Right("{[(V@l!d-0r!g!n4t*r-1D?)]}")
      }
    }

    "return Left (403 Forbidden)" when {
      "given an originatorId shorter than 3 characters" in {
        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> ("A" * 2)))
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        assertForbidden(result)
      }

      "given an originatorId longer than 40 characters" in {
        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> ("A" * 41)))
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        assertForbidden(result)
      }

      "given an originatorId contains a space" in {
        val request =
          generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> "contains space"))
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        assertForbidden(result)
      }

      "given an originatorId contains a tab" in {
        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> "tab\tchar"))
        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)

        assertForbidden(result)
      }
    }
  }
}
