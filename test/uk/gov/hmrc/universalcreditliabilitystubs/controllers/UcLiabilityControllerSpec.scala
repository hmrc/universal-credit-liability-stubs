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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.mvc.Results.{BadRequest, InternalServerError}
import play.api.test.Helpers.*
import uk.gov.hmrc.universalcreditliabilitystubs.config.AppConfig
import uk.gov.hmrc.universalcreditliabilitystubs.controllers.UcLiabilityController
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.{InsertLiabilityRequest, TerminateLiabilityRequest}
import uk.gov.hmrc.universalcreditliabilitystubs.services.{MappingService, SchemaValidationService}
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ErrorMessages.ForbiddenReason

import scala.concurrent.Future

class UcLiabilityControllerSpec extends AnyWordSpec with Matchers with TestHelpers with ScalaFutures {

  private val nino = generateNino()

  private val mockSchemaValidationService = mock[SchemaValidationService]
  private val mockMappingService          = mock[MappingService]
  private val mockAppConfig               = mock[AppConfig]

  when(mockAppConfig.hipClientId).thenReturn("local-client-id")
  when(mockAppConfig.hipClientSecret).thenReturn("local-client-secret")
  when(mockAppConfig.hipGovUkOriginatorId).thenReturn("TEST-GOV-UK-ORIGINATOR-ID")

  private val testUcLiabilityController = new UcLiabilityController(
    stubControllerComponents(),
    schemaValidationService = mockSchemaValidationService,
    mappingService = mockMappingService,
    appConfig = mockAppConfig
  )

  "UcLiabilityNotificationController.insertLiabilityDetails" must {
    "return 204 No Content" when {
      "given a valid Insert request" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = validHeaders)
        val result  = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe NO_CONTENT
      }
    }

    "return 400 BadRequest" when {
      "schema validation fails" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any())).thenReturn(Left(BadRequest))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = validHeaders)
        val result  = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe BAD_REQUEST
      }
    }

    "return 401 Unauthorized" when {
      "Authorization header is missing" in {
        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = missingAuthorizationHeader)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe UNAUTHORIZED
      }

      "Authorization header has invalid credentials" in {
        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = invalidAuthorizationHeader)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe UNAUTHORIZED
      }
    }

    "return 403 Forbidden" when {
      "the gov-uk-originator-id header missing" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = missingGovUkOriginatorIdHeader)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe FORBIDDEN
      }

      "the gov-uk-originator-id header value is invalid" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = invalidGovUkOriginatorIdHeader)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe FORBIDDEN
      }

      "the gov-uk-originator-id header value does not match the one provided" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = nonMatchingGovUkOriginatorIdHeader)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe FORBIDDEN
      }
    }

    "return 422 UnprocessableEntity" when {
      "NINO triggers a 422 error response" in {
        val test422Failure = Failure("End date before start date", "65537")

        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(Some(test422Failure))

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = validHeaders)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe UNPROCESSABLE_ENTITY
      }
    }

    "return the mapped system error" when {
      "NINO triggers a system error" in {
        when(mockSchemaValidationService.validateInsertLiabilityRequest(any(), any()))
          .thenReturn(Right(validInsertLiabilityRequest.as[InsertLiabilityRequest]))
        when(mockMappingService.mapSystemErrors(nino)).thenReturn(Some(InternalServerError))
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)

        val request = generateFakeRequest(requestBody = validInsertLiabilityRequest, headers = validHeaders)
        val result = testUcLiabilityController.insertLiabilityDetails(nino)(request)

        status(result) mustBe INTERNAL_SERVER_ERROR
      }
    }
  }

  "UcLiabilityNotificationController.terminateLiabilityDetails" must {

    "return 204 No Content" when {

      "given a valid Terminate request" in {
        when(mockSchemaValidationService.validateTerminateLiabilityRequest(any(), any()))
          .thenReturn(Right(validTerminateLiabilityRequest.as[TerminateLiabilityRequest]))

        when(mockMappingService.mapSystemErrors(nino)).thenReturn(None)
        when(mockMappingService.map422ErrorResponses(nino)).thenReturn(None)
        val request = generateFakeRequest(requestBody = validTerminateLiabilityRequest, headers = validHeaders)

        val result = testUcLiabilityController.terminateLiabilityDetails(nino)(request)

        status(result) mustBe NO_CONTENT
      }
    }
  }

//  "UcLiabilityNotificationController.insertLiabilityDetails bak" must {
//
//    "return right" when {
//      "given a valid GovUkOriginatorId provided by HIP" in {
//        when(mockAppConfig.hipGovUkOriginatorId).thenReturn("TEST-GOV-UK-ORIGINATOR-ID")
//
//        val request = generateFakeRequest(
//          requestBody = Json.obj(),
//          headers = Seq(GovUkOriginatorId -> mockAppConfig.hipGovUkOriginatorId)
//        )
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        result mustBe Right(mockAppConfig.hipGovUkOriginatorId)
//      }
//
//      "given a valid GovUkOriginatorId with special characters: '{}, [], (), @, !, *, -, ?'" in {
//        val validGovUkOriginatorId = "{[(V@l!d-0r!g!n4t*r-1D?)]}"
//        when(mockAppConfig.hipGovUkOriginatorId).thenReturn(validGovUkOriginatorId)
//
//        val request = generateFakeRequest(
//          requestBody = Json.obj(),
//          headers = Seq(GovUkOriginatorId -> validGovUkOriginatorId)
//        )
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        result mustBe Right(validGovUkOriginatorId)
//      }
//    }
//
//    "return Left (403 Forbidden)" when {
//      "given a GovUkOriginatorId that does not match the one provided by HIP" in {
//        val request = generateFakeRequest(
//          requestBody = Json.obj(),
//          headers = Seq(GovUkOriginatorId -> "NON-MATCHING-GOV-UK-ORIGINATOR-ID")
//        )
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//
//      "given a GovUkOriginatorId shorter than the minimum length of 3 characters" in {
//        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> ("A" * 2)))
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//
//      "given a GovUkOriginatorId longer than the maximum length of 40 characters" in {
//        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> ("A" * 41)))
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//
//      "given a GovUkOriginatorId contains a space" in {
//        val request =
//          generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> "contains space"))
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//
//      "given a GovUkOriginatorId contains a tab" in {
//        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> "tab\tchar"))
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//
//      "given a GovUkOriginatorId contains a new line" in {
//        val request = generateFakeRequest(requestBody = Json.obj(), headers = Seq(GovUkOriginatorId -> "new\nline"))
//        val result  = testUcLiabilityController.validateGovUkOriginatorId(request)
//
//        assertForbidden(result)
//      }
//    }
//  }

}
