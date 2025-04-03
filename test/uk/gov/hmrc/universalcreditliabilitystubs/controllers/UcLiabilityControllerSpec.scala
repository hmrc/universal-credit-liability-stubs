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

package uk.gov.hmrc.universalcreditliabilitystubs.controllers

import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}

class UcLiabilityControllerSpec extends AnyWordSpec with Matchers {

  private val fakeRequest = FakeRequest("POST", "/")
  private val controller  = new UcLiabilityController(Helpers.stubControllerComponents())

  "POST /" should {
    "return 204" in {
      val result = controller.submitLiabilityDetails("nino")(fakeRequest)
      status(result) mustBe Status.NO_CONTENT
    }
  }
}
