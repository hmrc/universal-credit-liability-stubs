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

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.*
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ValidationPatterns.DatePattern

final case class UcLiabilityTerminationDetails(
  universalCreditRecordType: UniversalCreditRecordType,
  liabilityStartDate: String,
  liabilityEndDate: String
)

object UcLiabilityTerminationDetails {

  private def isValidDate(value: String): Boolean = DatePattern.matches(value)

  private val validDate: Reads[String] = Reads.verifying[String](isValidDate)

  implicit val reads: Reads[UcLiabilityTerminationDetails] = (
    (JsPath \ "universalCreditRecordType").read[UniversalCreditRecordType] and
      (JsPath \ "liabilityStartDate").read(validDate) and
      (JsPath \ "liabilityEndDate").read(validDate)
  )(UcLiabilityTerminationDetails.apply _)

  implicit val writes: OWrites[UcLiabilityTerminationDetails] = Json.writes[UcLiabilityTerminationDetails]
}
