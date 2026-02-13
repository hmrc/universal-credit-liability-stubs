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
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.UniversalCreditRecordType.LCW_LCWRA
import uk.gov.hmrc.universalcreditliabilitystubs.support.TestHelpers

class UniversalCreditLiabilityDetailsSpec
    extends AnyWordSpec
    with ScalaCheckPropertyChecks
    with Matchers
    with TestHelpers {

  val ucDetailsGen: Gen[UniversalCreditLiabilityDetails] = for {
    recordType  <- ucRecordTypeGen
    dateOfBirth <- Gen.option(mixedDateGen)
    startDate   <- mixedDateGen
    endDate     <- Gen.option(mixedDateGen)
  } yield UniversalCreditLiabilityDetails(recordType, dateOfBirth, startDate, endDate)

  "UniversalCreditLiabilityDetails" must {

    "Serialize/deserialize valid dates correctly" in {
      val testJson = Json.parse("""
          |{
          |  "universalCreditRecordType": "LCW/LCWRA",
          |  "dateOfBirth": "2002-04-27",
          |  "liabilityStartDate": "2015-08-19",
          |  "liabilityEndDate": "2026-06-30"
          |}
          |""".stripMargin)

      val expectedUniversalCreditLiabilityDetails = UniversalCreditLiabilityDetails(
        universalCreditRecordType = LCW_LCWRA,
        dateOfBirth = Some("2002-04-27"),
        liabilityStartDate = "2015-08-19",
        liabilityEndDate = Some("2026-06-30")
      )

      val result = testJson.validate[UniversalCreditLiabilityDetails]

      result mustBe JsSuccess(expectedUniversalCreditLiabilityDetails)
    }

    "Fail deserialization for invalid dates" in {
      val testJson = Json.parse("""
          |{
          |  "universalCreditRecordType": "LCW/LCWRA",
          |  "dateOfBirth": "2002-99-99",
          |  "liabilityStartDate": "2025-13-35",
          |  "liabilityEndDate": "2026-14-40"
          |}
          |""".stripMargin)

      val result = testJson.validate[UniversalCreditLiabilityDetails]

      result.isError mustBe true
    }
  }
}
