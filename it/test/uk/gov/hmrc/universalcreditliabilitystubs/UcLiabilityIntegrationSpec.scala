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
import play.api.http.Status.{BAD_REQUEST, FORBIDDEN, INTERNAL_SERVER_ERROR, NOT_FOUND, NO_CONTENT, SERVICE_UNAVAILABLE, UNAUTHORIZED, UNPROCESSABLE_ENTITY}
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSClient, readableAsJson, readableAsString}
import uk.gov.hmrc.universalcreditliabilitystubs.helpers.{OpenApiValidator, ValidationError}
import uk.gov.hmrc.universalcreditliabilitystubs.services.SchemaValidationService.CorrelationIdPattern
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames

class UcLiabilityIntegrationSpec
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with TestHelpers {

  private given WSClient = app.injector.instanceOf[WSClient]

  private val validNino: String             = generateNino()
  private val faultyInsertionNino: String   = generateNinoWithPrefix("BW130")
  private val faultyTerminationNino: String = generateNinoWithPrefix("BZ230")

  private val notFoundErrorNino: String       = generateNinoWithPrefix("XY404")
  private val internalServerErrorNino: String = generateNinoWithPrefix("XY500")
  private val serviceUnavailableNino: String  = generateNinoWithPrefix("XY503")

  private def buildInsertionUrl(nino: String)   = s"/ni/person/$nino/liability/universal-credit"
  private def buildTerminationUrl(nino: String) = s"/ni/person/$nino/liability/universal-credit/termination"

  private def insertionUrl   = buildInsertionUrl(validNino)
  private def terminationUrl = buildTerminationUrl(validNino)

  private def insertionUrlWithFaultyNino   = buildInsertionUrl(faultyInsertionNino)
  private def terminationUrlWithFaultyNino = buildTerminationUrl(faultyTerminationNino)

  private def insertionUrlWith404Nino   = buildInsertionUrl(notFoundErrorNino)
  private def terminationUrlWith404Nino = buildTerminationUrl(notFoundErrorNino)

  private def insertionUrlWith500Nino   = buildInsertionUrl(internalServerErrorNino)
  private def terminationUrlWith500Nino = buildTerminationUrl(internalServerErrorNino)

  private def insertionUrlWith503Nino   = buildInsertionUrl(serviceUnavailableNino)
  private def terminationUrlWith503Nino = buildTerminationUrl(serviceUnavailableNino)

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

    "respond with 400 status with no body and an auto generated correlationId header when request correlationId is missing" in {
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

    "respond with 401 status when Authorization header is missing" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrl)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(missingAuthorizationHeader: _*)
        .withBody(invalidInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe UNAUTHORIZED
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
      requestValidationErrors must not be List.empty[ValidationError]

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

    "respond with 404 status when NINO matches the criteria for a 404 case" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrlWith404Nino)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(validHeaders: _*)
        .withBody(validInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)
      requestValidationErrors mustBe List.empty[ValidationError]

      val response =
        request
          .execute()
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NOT_FOUND
      response.body[String] mustBe ""

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = insertionPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 422 status when NINO matches the criteria any of the 422 cases" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrlWithFaultyNino)

      val request = insertionPathValidator
        .newRequestBuilder()
        .withHttpHeaders(validHeaders: _*)
        .withBody(validInsertLiabilityRequest)

      val requestValidationErrors: List[ValidationError] = insertionPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response = request.execute().futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe UNPROCESSABLE_ENTITY
      response.body[JsValue] mustBe Json.parse("""
          |{
          |  "failures": [
          |    {
          |      "reason": "Start Date and End Date must be earlier than Date of Death",
          |      "code": "55006"
          |    }
          |  ]
          |}
          |""".stripMargin)

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 500 status when NINO matches the criteria for a 500 case" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrlWith500Nino)

      val request =
        insertionPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe INTERNAL_SERVER_ERROR

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = insertionPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 503 status when NINO matches the criteria for a 503 case" in {
      val insertionPathValidator = openApiValidator.forPath("POST", insertionUrlWith503Nino)

      val request =
        insertionPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validInsertLiabilityRequest)

      val requestValidationErrors = insertionPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response =
        request
          .execute()
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe SERVICE_UNAVAILABLE

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

    "respond with 400 status with no body and an auto generated correlationId header when request correlationId is missing" in {
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

    "respond with 401 status when Authorization header is missing" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request = terminationPathValidator
        .newRequestBuilder()
        .withHttpHeaders(missingAuthorizationHeader: _*)
        .withBody(inValidTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe UNAUTHORIZED
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 403 status when originator id is missing" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrl)

      val request = terminationPathValidator
        .newRequestBuilder()
        .withHttpHeaders(missingOriginatorIdHeader: _*)
        .withBody(inValidTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors must not be List.empty[ValidationError]

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

    "respond with 404 status when NINO matches the criteria for a 404 case" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrlWith404Nino)

      val request = terminationPathValidator
        .newRequestBuilder()
        .withHttpHeaders(validHeaders: _*)
        .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)
      requestValidationErrors mustBe List.empty[ValidationError]

      val response =
        request
          .execute()
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NOT_FOUND
      response.body[String] mustBe ""

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = terminationPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 422 status when NINO matches the criteria for any of the 422 cases" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrlWithFaultyNino)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response = request
        .execute()
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe UNPROCESSABLE_ENTITY
      response.body[JsValue] mustBe Json.parse("""
          |{
          |  "failures": [
          |    {
          |      "reason": "The NINO input matches an account that has been transferred to the Isle of Man",
          |      "code": "65543"
          |    }
          |  ]
          |}
          |""".stripMargin)

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = terminationPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 500 status when NINO matches the criteria for a 500 case" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrlWith500Nino)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response =
        request
          .execute()
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe INTERNAL_SERVER_ERROR

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern

      val responseValidationErrors = terminationPathValidator.validateResponse(response)
      responseValidationErrors mustBe List.empty
    }

    "respond with 503 status when NINO matches the criteria for a 503 case" in {
      val terminationPathValidator = openApiValidator.forPath("POST", terminationUrlWith503Nino)

      val request =
        terminationPathValidator
          .newRequestBuilder()
          .withHttpHeaders(validHeaders: _*)
          .withBody(validTerminateLiabilityRequest)

      val requestValidationErrors = terminationPathValidator.validateRequest(request)

      requestValidationErrors mustBe List.empty[ValidationError]

      val response =
        request
          .execute()
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe SERVICE_UNAVAILABLE

      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }
  }

}
