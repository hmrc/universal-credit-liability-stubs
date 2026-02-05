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

package uk.gov.hmrc.universalcreditliabilitystubs.models.request

import org.scalacheck.Gen
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.Json
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.UniversalCreditRecordType.LCW_LCWRA
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ValidationPatterns.DatePattern

class UniversalCreditLiabilityDetailsSpec
    extends AnyWordSpec
    with ScalaCheckPropertyChecks
    with Matchers
    with TestHelpers {

  val ucDetailsGen: Gen[UniversalCreditLiabilityDetails] = for {
    recordType  <- ucRecordTypeGen
    dateOfBirth <- mixedDateGen
    startDate   <- mixedDateGen
    endDate     <- mixedDateGen
  } yield UniversalCreditLiabilityDetails(recordType, Some(dateOfBirth), startDate, Some(endDate))

  "UniversalCreditLiabilityDetails" must {

    "Serialize/deserialize valid dates correctly" in
      forAll(ucDetailsGen, minSuccessful(1000)) { detail =>
        whenever(
          detail.dateOfBirth.exists(DatePattern.matches) && DatePattern.matches(detail.liabilityStartDate) &&
            detail.liabilityEndDate.exists(DatePattern.matches)
        ) {
          val json   = Json.toJson(detail)
          val parsed = json.validate[UniversalCreditLiabilityDetails]

          parsed.isSuccess mustBe true
          parsed.get mustEqual detail
        }
      }

    "Fail deserialization for invalid dates" in
      forAll(ucDetailsGen, minSuccessful(1000)) { detail =>
        whenever(
          !detail.dateOfBirth.exists(DatePattern.matches) || !DatePattern.matches(detail.liabilityStartDate) ||
            !detail.liabilityEndDate.exists(DatePattern.matches)
        ) {
          val json   = Json.toJson(detail)
          val parsed = json.validate[UniversalCreditLiabilityDetails]

          parsed.isError mustBe true
        }
      }

    "Handle optional dateOfBirth" when {

      "dateOfBirth is None" in {
        val detail = UniversalCreditLiabilityDetails(
          universalCreditRecordType = LCW_LCWRA,
          dateOfBirth = None,
          liabilityStartDate = "2024-01-15",
          liabilityEndDate = Some("2024-12-31")
        )

        val json   = Json.toJson(detail)
        val parsed = json.validate[UniversalCreditLiabilityDetails]

        parsed.isSuccess mustBe true
        parsed.get.dateOfBirth mustBe None
      }

      "dateOfBirth is valid" in {
        val detail = UniversalCreditLiabilityDetails(
          universalCreditRecordType = LCW_LCWRA,
          dateOfBirth = Some("1990-05-20"),
          liabilityStartDate = "2024-01-15",
          liabilityEndDate = Some("2024-12-31")
        )

        val json   = Json.toJson(detail)
        val parsed = json.validate[UniversalCreditLiabilityDetails]

        parsed.isSuccess mustBe true
        parsed.get.dateOfBirth mustBe Some("1990-05-20")
      }
    }
  }

}
