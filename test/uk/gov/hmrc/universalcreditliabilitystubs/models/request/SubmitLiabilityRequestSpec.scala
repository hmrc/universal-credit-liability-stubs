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

import java.time.LocalDate

class SubmitLiabilityRequestSpec extends AnyWordSpec with Matchers {

  "must write to correct json" in {

    val model = SubmitLiabilityRequest(
      universalCreditLiabilityDetail = UniversalCreditLiabilityDetail(
        universalCreditRecordType = "LCW/LCWRA",
        universalCreditAction = "Insert",
        dateOfBirth = LocalDate.of(2002, 10, 10),
        liabilityStartDate = LocalDate.of(2015, 8, 19),
        liabilityEndDate = Some(LocalDate.of(2025, 1, 4))
      )
    )

    val expectedJson = Json.obj(
      "universalCreditLiabilityDetail" -> Json.obj(
        "universalCreditRecordType" -> "LCW/LCWRA",
        "universalCreditAction"     -> "Insert",
        "dateOfBirth"               -> "2002-10-10",
        "liabilityStartDate"        -> "2015-08-19",
        "liabilityEndDate"          -> "2025-01-04"
      )
    )

    Json.toJson(model) mustBe expectedJson
  }

}
