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
import play.api.libs.ws.WSClient
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

  "Insert UC Liability endpoint" must {
    "respond with 204 status" in {
      val response = wsUrl(insertionUrl)
        .withHttpHeaders(validHeaders: _*)
        .withBody(validInsertLiabilityRequest)
        .execute("POST")
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NO_CONTENT
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 400 status" in {
      val response = wsUrl(insertionUrl)
        .withHttpHeaders(validHeaders: _*)
        .withBody(invalidInsertLiabilityRequest)
        .execute("POST")
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 403 status" in {
      val response = wsUrl(insertionUrl)
        .withHttpHeaders(missingOriginatorIdHeader: _*)
        .withBody(invalidInsertLiabilityRequest)
        .execute("POST")
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe FORBIDDEN
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 400 status and return a correlationid header" in {
      val response = wsUrl(insertionUrl)
        .withHttpHeaders(missingCorrelationIdHeader: _*)
        .withBody(validInsertLiabilityRequest)
        .execute("POST")
        .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }
  }

  "Terminate UC Liability endpoint" must {
    "respond with 204 status" in {
      val response =
        wsUrl(terminationUrl)
          .withHttpHeaders(validHeaders: _*)
          .withBody(validTerminateLiabilityRequest)
          .execute("POST")
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe NO_CONTENT
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 400 status" in {
      val response =
        wsUrl(terminationUrl)
          .withHttpHeaders(validHeaders: _*)
          .withBody(inValidTerminateLiabilityRequest)
          .execute("POST")
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 403 status" in {
      val response =
        wsUrl(terminationUrl)
          .withHttpHeaders(missingOriginatorIdHeader: _*)
          .withBody(inValidTerminateLiabilityRequest)
          .execute("POST")
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe FORBIDDEN
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }

    "respond with 400 status and return a correlationid header" in {
      val response =
        wsUrl(terminationUrl)
          .withHttpHeaders(missingCorrelationIdHeader: _*)
          .withBody(validTerminateLiabilityRequest)
          .execute("POST")
          .futureValue

      val correlationId = response.headers.get(HeaderNames.CorrelationId).flatMap(_.headOption)

      response.status mustBe BAD_REQUEST
      correlationId mustBe defined
      correlationId.get must fullyMatch regex CorrelationIdPattern
    }
  }
}
