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
import play.api.mvc.Results.{BadRequest, Forbidden, InternalServerError, NotFound, ServiceUnavailable, Unauthorized}
import uk.gov.hmrc.http.UnauthorizedException
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers

class MappingServiceSpec extends AnyWordSpec with Matchers with TestHelpers {

  "MappingService.mapSystemErrors" must {
    "return a BadRequest (400) when NINO starts with AE400" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE400"))
      result mustBe Some(BadRequest)
    }

    "return a Unauthorized (401) when NINO starts with AE401" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE401"))
      result mustBe Some(Unauthorized)
    }

    "return a Forbidden (403) when NINO starts with AE403" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE403"))
      result mustBe Some(Forbidden)
    }

    "return a NotFound (404) when NINO starts with AE404" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE404"))
      result mustBe Some(NotFound)
    }

    "return a InternalServerError (500) when NINO starts with AE500" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE500"))
      result mustBe Some(InternalServerError)
    }

    "return a ServiceUnavailable (503) when NINO starts with AE503" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("AE503"))
      result mustBe Some(ServiceUnavailable)
    }

    "not return an system error when NINO starts with none of the above prefixes" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("ZZ000"))
      result mustBe None
    }
  }

  "MappingService.map422ErrorResponses" must {
    "return a 422 with code '55006' when NINO starts with BE001" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE001"))
      result mustBe Some(Failure("Start Date and End Date must be earlier than Date of Death", "55006"))
    }

    "return a 422 with code '55008' when NINO starts with BE002" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE002"))
      result mustBe Some(Failure("End Date must be earlier than State Pension Age", "55008"))
    }

    "return a 422 with code '55027' when NINO starts with BE003" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE003"))
      result mustBe Some(Failure("End Date later than Date of Death", "55027"))
    }

    "return a 422 with code '55029' when NINO starts with BE004" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE004"))
      result mustBe Some(Failure("Start Date later than SPA", "55029"))
    }

    "return a 422 with code '55038' when NINO starts with BE005" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE005"))
      result mustBe Some(Failure("A conflicting or identical Liability is already recorded", "55038"))
    }

    "return a 422 with code '55039' when NINO starts with BE006" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE006"))
      result mustBe Some(Failure("NO corresponding liability found", "55039"))
    }

    "return a 422 with code '64996' when NINO starts with BE007" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE007"))
      result mustBe Some(Failure("Start Date is not before date of death", "64996"))
    }

    "return a 422 with code '64997' when NINO starts with BE008" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE008"))
      result mustBe Some(Failure("LCW/LCWRA not within a period of UC", "64997"))
    }

    "return a 422 with code '64998' when NINO starts with BE009" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE009"))
      result mustBe Some(Failure("LCW/LCWRA Override not within a period of LCW/LCWRA", "64998"))
    }

    "return a 422 with code '65026' when NINO starts with BE010" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE010"))
      result mustBe Some(Failure("Start date must not be before 16th birthday", "65026"))
    }

    "return a 422 with code '65536' when NINO starts with BE011" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE011"))
      result mustBe Some(Failure("Start date before 29/04/2013", "65536"))
    }

    "return a 422 with code '65537' when NINO starts with BE012" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE012"))
      result mustBe Some(Failure("End date before start date", "65537"))
    }

    "return a 422 with code '65541' when NINO starts with BE013" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE013"))
      result mustBe Some(Failure("The NINO input matches a Pseudo Account", "65541"))
    }

    "return a 422 with code '65542' when NINO starts with BE014" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE014"))
      result mustBe Some(
        Failure(
          "The NINO input matches a non-live account (including redundant, amalgamated and administrative account types)",
          "65542"
        )
      )
    }

    "return a 422 with code '65543' when NINO starts with BE015" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE015"))
      result mustBe Some(
        Failure("The NINO input matches an account that has been transferred to the Isle of Man", "65543")
      )
    }

    "return a 422 with code '99999' when NINO starts with BE016" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BE016"))
      result mustBe Some(Failure("Start Date after Death", "99999"))
    }

    "not return an error response when NINO starts with none of the above prefixes" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("ZZ000"))
      result mustBe None
    }
  }

}
