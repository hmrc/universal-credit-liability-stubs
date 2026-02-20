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
import play.api.mvc.Results.*
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers

class MappingServiceSpec extends AnyWordSpec with Matchers with TestHelpers {

  "MappingService.mapSystemErrors" must {
    "return a BadRequest (400) when NINO starts with XY400" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY400"))
      result mustBe Some(BadRequest)
    }

    "return a Unauthorized (401) when NINO starts with XY401" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY401"))
      result mustBe Some(Unauthorized)
    }

    "return a Forbidden (403) when NINO starts with XY403" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY403"))
      result mustBe Some(Forbidden)
    }

    "return a Forbidden (403) when NINO starts with HJ120" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("HJ120"))
      result mustBe Some(Forbidden)
    }

    "return a NotFound (404) when NINO starts with XY404" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY404"))
      result mustBe Some(NotFound)
    }

    "return a NotFound (404) when NINO starts with CM110" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("CM110"))
      result mustBe Some(NotFound)
    }

    "return a InternalServerError (500) when NINO starts with XY500" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY500"))
      result mustBe Some(InternalServerError)
    }

    "return a InternalServerError (500) when NINO starts with HZ020" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("HZ020"))
      result mustBe Some(InternalServerError)
    }

    "return a ServiceUnavailable (503) when NINO starts with XY503" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("XY503"))
      result mustBe Some(ServiceUnavailable)
    }

    "not return an system error when NINO starts with none of the above prefixes" in {
      val result = mappingService.mapSystemErrors(generateNinoWithPrefix("ZZ000"))
      result mustBe None
    }
  }

  "MappingService.map422ErrorResponses" must {
    "return a 422 with code '55006' when NINO starts with BW130" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BW130"))
      result mustBe Some(Failure("Start Date and End Date must be earlier than Date of Death", "55006"))
    }

    "return a 422 with code '55008' when NINO starts with EZ200" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("EZ200"))
      result mustBe Some(Failure("End Date must be earlier than State Pension Age", "55008"))
    }

    "return a 422 with code '55027' when NINO starts with BK190" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BK190"))
      result mustBe Some(Failure("End Date later than Date of Death", "55027"))
    }

    "return a 422 with code '55029' when NINO starts with ET060" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("ET060"))
      result mustBe Some(Failure("Start Date later than SPA", "55029"))
    }

    "return a 422 with code '55038' when NINO starts with GE100" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("GE100"))
      result mustBe Some(Failure("A conflicting or identical Liability is already recorded", "55038"))
    }

    "return a 422 with code '55039' when NINO starts with GP050" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("GP050"))
      result mustBe Some(Failure("NO corresponding liability found", "55039"))
    }

    "return a 422 with code '64996' when NINO starts with EK310" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("EK310"))
      result mustBe Some(Failure("Start Date is not before date of death", "64996"))
    }

    "return a 422 with code '64997' when NINO starts with HS260" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("HS260"))
      result mustBe Some(Failure("LCW/LCWRA not within a period of UC", "64997"))
    }

    "return a 422 with code '64998' when NINO starts with CE150" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("CE150"))
      result mustBe Some(Failure("LCW/LCWRA Override not within a period of LCW/LCWRA", "64998"))
    }

    "return a 422 with code '65026' when NINO starts with HC210" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("HC210"))
      result mustBe Some(Failure("Start date must not be before 16th birthday", "65026"))
    }

    "return a 422 with code '65536' when NINO starts with GX240" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("GX240"))
      result mustBe Some(Failure("Start date before 29/04/2013", "65536"))
    }

    "return a 422 with code '65537' when NINO starts with HT230" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("HT230"))
      result mustBe Some(Failure("End date before start date", "65537"))
    }

    "return a 422 with code '65541' when NINO starts with BX100" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BX100"))
      result mustBe Some(Failure("The NINO input matches a Pseudo Account", "65541"))
    }

    "return a 422 with code '65542' when NINO starts with HZ310" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("HZ310"))
      result mustBe Some(
        Failure(
          "The NINO input matches a non-live account (including redundant, amalgamated and administrative account types)",
          "65542"
        )
      )
    }

    "return a 422 with code '65543' when NINO starts with BZ230" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("BZ230"))
      result mustBe Some(
        Failure("The NINO input matches an account that has been transferred to the Isle of Man", "65543")
      )
    }

    "return a 422 with code '99999' when NINO starts with AB150" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AB150"))
      result mustBe Some(Failure("Start Date after Death", "99999"))
    }

    "not return an error response when NINO starts with none of the above prefixes" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("ZZ000"))
      result mustBe None
    }
  }

}
