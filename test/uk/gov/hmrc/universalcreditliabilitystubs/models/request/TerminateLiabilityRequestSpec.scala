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

class TerminateLiabilityRequestSpec extends AnyWordSpec with Matchers {

  "TerminateLiabilityRequest" must {

    "parse request jsons correctly" in {
      val jsonString =
        """
          |{
          |"ucLiabilityTerminationDetails": {
          |"universalCreditRecordType": "LCW/LCWRA",
          |"liabilityStartDate": "2025-08-19",
          |"liabilityEndDate": "2026-06-30"
          |}
          |}
          |""".stripMargin

      Json.parse(jsonString).as[TerminateLiabilityRequest] mustBe TerminateLiabilityRequest(
        ucLiabilityTerminationDetails = UcLiabilityTerminationDetails(
          universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
          liabilityStartDate = "2015-08-19",
          liabilityEndDate = "2026-06-30"
        )
      )
    }

    "must write to correct json" in {

      val model = TerminateLiabilityRequest(
        ucLiabilityTerminationDetails = UcLiabilityTerminationDetails(
          universalCreditRecordType = UniversalCreditRecordType.LCW_LCWRA,
          liabilityStartDate = "2015-08-19",
          liabilityEndDate = "2026-06-30"
        )
      )

      val expectedJson = Json.obj(
        "ucLiabilityTerminationDetails" -> Json.obj(
          "universalCreditRecordType" -> "LCW/LCWRA",
          "liabilityStartDate"        -> "2015-08-19",
          "liabilityEndDate"          -> "2026-06-30"
        )
      )

      Json.toJson(model) mustBe expectedJson
    }
  }
}
