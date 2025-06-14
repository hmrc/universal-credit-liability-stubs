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
import play.api.mvc.Results.BadRequest
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.*
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.PathParameter.Nino
import uk.gov.hmrc.universalcreditliabilitystubs.utils.{ApplicationConstants, HeaderNames}

class SchemaValidationServiceSpec extends AnyWordSpec with Matchers with TestHelpers {

  "validateInsertLiabilityRequest" must {

    "return a SubmitLiabilityRequest object given an valid request body" in {

      val result =
        schemaValidationService.validateInsertLiabilityRequest(
          generateFakeRequest(validInsertLiabilityRequest, validHeaders),
          generateNino()
        )

      result mustBe Right(
        InsertLiabilityRequest(
          universalCreditLiabilityDetails = UniversalCreditLiabilityDetails(
            universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
            dateOfBirth = "2002-10-10",
            liabilityStartDate = "2015-08-19",
            liabilityEndDate = Some("2025-01-04")
          )
        )
      )
    }

    "return a BadRequest Result for input parameter: nino given an invalid nino" in {

      val result =
        schemaValidationService.validateInsertLiabilityRequest(
          generateFakeRequest(validInsertLiabilityRequest, validHeaders),
          Nino
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for input parameter: correlationId given an invalid correlationId" in {

      val inValidHeaders: Seq[(String, String)] = Seq(
        HeaderNames.CorrelationId -> "3e8dae97-b586-4cef-8511"
      )

      val result =
        schemaValidationService.validateInsertLiabilityRequest(
          generateFakeRequest(validInsertLiabilityRequest, inValidHeaders),
          generateNino()
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for parameter: universalCreditLiabilityDetails/liabilityStartDate given an invalid request body" in {

      val result =
        schemaValidationService.validateInsertLiabilityRequest(
          generateFakeRequest(invalidInsertLiabilityRequest, validHeaders),
          generateNino()
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for multiple missing parameter given an invalid request body and invalid nino" in {

      val result = schemaValidationService.validateInsertLiabilityRequest(
        generateFakeRequest(invalidInsertLiabilityRequest, validHeaders),
        "AA1234"
      )

      result mustBe Left(BadRequest)
    }
  }

  "validateTerminateLiabilityRequest" must {

    "return a SubmitLiabilityRequest object given an valid request body" in {

      val result =
        schemaValidationService.validateTerminateLiabilityRequest(
          generateFakeRequest(validTerminateLiabilityRequest, validHeaders),
          generateNino()
        )

      result mustBe Right(
        TerminateLiabilityRequest(
          ucLiabilityTerminationDetails = UcLiabilityTerminationDetails(
            universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
            liabilityStartDate = "2015-08-19",
            liabilityEndDate = "2025-01-04"
          )
        )
      )
    }

    "return a BadRequest Result for input parameter: nino given an invalid nino" in {

      val result =
        schemaValidationService.validateTerminateLiabilityRequest(
          generateFakeRequest(validTerminateLiabilityRequest, validHeaders),
          Nino
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for input parameter: correlationId given an invalid correlationId" in {

      val inValidHeaders: Seq[(String, String)] = Seq(
        HeaderNames.CorrelationId -> "3e8dae97-b586-4cef-8511"
      )

      val result =
        schemaValidationService.validateTerminateLiabilityRequest(
          generateFakeRequest(validTerminateLiabilityRequest, inValidHeaders),
          generateNino()
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for parameter: ucLiabilityTerminationDetails/liabilityEndDate given an invalid request body" in {

      val result =
        schemaValidationService.validateTerminateLiabilityRequest(
          generateFakeRequest(inValidTerminateLiabilityRequest, validHeaders),
          generateNino()
        )

      result mustBe Left(BadRequest)
    }

    "return a BadRequest Result for multiple missing parameter given an invalid request body and invalid nino" in {

      val result = schemaValidationService.validateTerminateLiabilityRequest(
        generateFakeRequest(inValidTerminateLiabilityRequest, validHeaders),
        "AA1234"
      )

      result mustBe Left(BadRequest)
    }
  }
}
