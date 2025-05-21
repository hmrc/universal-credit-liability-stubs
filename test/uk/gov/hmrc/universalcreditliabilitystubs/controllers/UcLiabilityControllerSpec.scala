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

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.http.Status
import play.api.test.Helpers
import play.api.test.Helpers.*
import uk.gov.hmrc.universalcreditliabilitystubs.services.UcLiabilityService
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers

class UcLiabilityControllerSpec extends AnyWordSpec with TestHelpers with Matchers {

  private val mockUcLiabilityService = mock[UcLiabilityService]
  private val controller             = new UcLiabilityController(Helpers.stubControllerComponents(), mockUcLiabilityService)

  "insertLiabilityDetails" must {
    "return 204" in {

      val fakeRequest = generateFakeRequest(validInsertLiabilityRequest, validHeaders)

      when(mockUcLiabilityService.validateInsertLiabilityRequest(any(), any()))
        .thenReturn(Right(getInsertLiabilityRequest))

      val result =
        controller.insertLiabilityDetails(generateNino())(fakeRequest)
      status(result) mustBe Status.NO_CONTENT
    }
  }

  "terminateLiabilityDetails" must {
    "return 204" in {
      val fakeRequest = generateFakeRequest(validTerminateLiabilityRequest, validHeaders)

      when(mockUcLiabilityService.validateTerminateLiabilityRequest(any(), any()))
        .thenReturn(Right(getTerminateLiabilityRequest))

      val result =
        controller.terminateLiabilityDetails(generateNino())(fakeRequest)
      status(result) mustBe Status.NO_CONTENT
    }
  }
}
