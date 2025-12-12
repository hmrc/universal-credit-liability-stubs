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
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers

class MappingServiceSpec extends AnyWordSpec with Matchers with TestHelpers {

  "MappingService.map422ErrorResponses" must {

    "return a 55006 failure when a NINO starts with AA01" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA01"))
      result mustBe Some(Failure("Start Date and End Date must be earlier than Date of Death", "55006"))
    }

    "return a 55008 failure when a NINO starts with AA02" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA02"))
      result mustBe Some(Failure("End Date must be earlier than State Pension Age", "55008"))
    }

    "return a 55027 failure when a NINO starts with AA03" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA03"))
      result mustBe Some(Failure("End Date later than Date of Death", "55027"))
    }

    "return a 55029 failure when a NINO starts with AA04" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA04"))
      result mustBe Some(Failure("Start Date later than SPA", "55029"))
    }

    "return a 55038 failure when a NINO starts with AA05" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA05"))
      result mustBe Some(Failure("A conflicting or identical Liability is already recorded", "55038"))
    }

    "return a 55039 failure when a NINO starts with AA06" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA06"))
      result mustBe Some(Failure("NO corresponding liability found", "55039"))
    }

    "return a 64996 failure when a NINO starts with AA07" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA07"))
      result mustBe Some(Failure("Start Date is not before date of death", "64996"))
    }

    "return a 64997 failure when a NINO starts with AA08" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA08"))
      result mustBe Some(Failure("LCW/LCWRA not within a period of UC", "64997"))
    }

    "return a 64998 failure when a NINO starts with AA09" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA09"))
      result mustBe Some(Failure("LCW/LCWRA Override not within a period of LCW/LCWRA", "64998"))
    }

    "return a 65026 failure when a NINO starts with AA10" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA10"))
      result mustBe Some(Failure("Start date must not be before 16th birthday", "65026"))
    }

    "return a 65536 failure when a NINO starts with AA11" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA11"))
      result mustBe Some(Failure("Start date before 29/04/2013", "65536"))
    }

    "return a 65537 failure when a NINO starts with AA12" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA12"))
      result mustBe Some(Failure("End date before start date", "65537"))
    }

    "return a 65541 failure when a NINO starts with AA13" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA13"))
      result mustBe Some(Failure("The NINO input matches a Pseudo Account", "65541"))
    }

    "return a 65542 failure when a NINO starts with AA14" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA14"))
      result mustBe Some(
        Failure(
          "The NINO input matches a non-live account (including redundant, amalgamated and administrative account types)",
          "65542"
        )
      )
    }

    "return a 65543 failure when a NINO starts with AA15" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA15"))
      result mustBe Some(
        Failure("The NINO input matches an account that has been transferred to the Isle of Man", "65543")
      )
    }

    "return a 99999 failure when a NINO starts with AA16" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA16"))
      result mustBe Some(Failure("Start Date after Death", "99999"))
    }

    "not return a failure when a NINO starts with none of the above prefixes" in {
      val result = mappingService.map422ErrorResponses(generateNinoWithPrefix("AA00"))
      result mustBe None
    }

  }

}
