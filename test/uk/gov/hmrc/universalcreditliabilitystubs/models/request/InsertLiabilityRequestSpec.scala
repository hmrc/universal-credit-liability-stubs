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

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class InsertLiabilityRequestSpec extends AnyWordSpec with Matchers {

  "SubmitLiabilityRequest" must {

    "parse request jsons correctly" in {
      val jsonString =
        """
          |{
          |  "universalCreditLiabilityDetails": {
          |    "universalCreditRecordType": "LCW/LCWRA",
          |    "dateOfBirth": "2002-10-10",
          |    "liabilityStartDate": "2015-08-19",
          |    "liabilityEndDate": "2025-01-04"
          |  }
          |}
          |""".stripMargin

      Json.parse(jsonString).as[InsertLiabilityRequest] mustBe InsertLiabilityRequest(
        universalCreditLiabilityDetails = UniversalCreditLiabilityDetails(
          universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
          dateOfBirth = "2002-10-10",
          liabilityStartDate = "2015-08-19",
          liabilityEndDate = Some("2025-01-04")
        )
      )
    }

    "must write to correct json" in {

      val model = InsertLiabilityRequest(
        universalCreditLiabilityDetails = UniversalCreditLiabilityDetails(
          universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
          dateOfBirth = "2002-10-10",
          liabilityStartDate = "2015-08-19",
          liabilityEndDate = Some("2025-01-04")
        )
      )

      val expectedJson = Json.obj(
        "universalCreditLiabilityDetails" -> Json.obj(
          "universalCreditRecordType" -> "LCW/LCWRA",
          "dateOfBirth"               -> "2002-10-10",
          "liabilityStartDate"        -> "2015-08-19",
          "liabilityEndDate"          -> "2025-01-04"
        )
      )

      Json.toJson(model) mustBe expectedJson
    }
  }
}
