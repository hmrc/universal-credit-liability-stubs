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

package uk.gov.hmrc.universalcreditliabilitystubs

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, NO_CONTENT}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSClient, readableAsString}
import uk.gov.hmrc.universalcreditliabilitystubs.helpers.OpenApiValidator
import uk.gov.hmrc.universalcreditliabilitystubs.services.SchemaValidationService.CorrelationIdPattern
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames

class UcLiabilityIntegrationSpec
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with TestHelpers {

  private given WSClient     = app.injector.instanceOf[WSClient]
  private def terminationUrl = s"/person/${generateNino()}/liability/universal-credit/termination"
  private def insertionUrl   = s"/person/${generateNino()}/liability/universal-credit"

  private val openApiValidator = OpenApiValidator.fromResource("openapi.hip.jf18645.2.0.1.yaml")

  "Insert UC Liability endpoint" must {
    "respond with 204 status when the request is valid" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrl)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(validHeaders: _*)
        .withBody(validInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors mustBe List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NO_CONTENT
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = insertionPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 400 status with no body when the request is bad" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrl)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(validHeaders: _*)
        .withBody(invalidInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      response.body[String] mustBe ""
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 403 status when originator id is missing" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrl)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(missingOriginatorIdHeader: _*)
        .withBody(invalidInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe FORBIDDEN
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = insertionPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
     }

    "respond with 400 status with no body and an auto generated correlationid header when request correlationid is missing" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrl)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(missingCorrelationIdHeader: _*)
        .withBody(validInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      response.body[String] mustBe ""
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }
  }

  "Terminate UC Liability endpoint" must {

    "respond with 204 status when the request is valid" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors mustBe List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NO_CONTENT
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = terminationPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 400 status with no body when the request is bad" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(inValidTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      response.body[String] mustBe ""
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 403 status" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(missingOriginatorIdHeader: _*)
          .withBody(inValidTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe FORBIDDEN
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = terminationPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 400 status with no body and an auto generated correlationid header when request correlationid is missing" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(missingCorrelationIdHeader: _*)
          .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      response.body[String] mustBe ""
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }
  }
}
