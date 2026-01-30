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

package uk.gov.hmrc.universalcreditliabilityapi.services

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.test.Helpers.*
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames.GovUkOriginatorId

import scala.concurrent.Future

class UcLiabilityControllerSpec extends AnyWordSpec with Matchers with TestHelpers with ScalaFutures{

  val testUcLiabilityController = new UcLiabilityControllerSpec()

  private def assertForbidden(result: Either[Future[Result], _]): Unit =
    whenReady(extractLeftOrFail(result)) { actualResult =>
      actualResult.header.status mustBe FORBIDDEN

      val body = contentAsJson(Future.successful(actualResult))
      (body \ "code").as[String] mustBe ApplicationConstants.ErrorCodes.ForbiddenCode
      (body \ "reason").as[String] mustBe ApplicationConstants.ForbiddenReason
    }

  "UcLiabilityController.validateGovUkOriginatorId" must {
    "return Forbidden when originatorId is shorter than 3 characters" in {
      val json = Json.obj()
      val request = buildFakeRequest(payload = json, headers = GovUkOriginatorId -> "AA")
      val result = testUcLiabilityController.validateGovUkOriginatorId(request) 
      assertForbidden (result)
    }
    "return Forbidden when originatorId is longer than 40 characters" in {
      val json = Json.obj()
      val request = buildFakeRequest(payload = json, headers = GovUkOriginatorId -> ("A" * 41))
      val result = testUcLiabilityController.validateGovUkOriginatorId(request) a
        ssertForbidden (result)
    }
  }
}
