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
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ValidationPatterns.DatePattern

final case class UniversalCreditLiabilityDetails(
  universalCreditRecordType: UniversalCreditRecordType,
  dateOfBirth: String,
  liabilityStartDate: String,
  liabilityEndDate: Option[String]
)

object UniversalCreditLiabilityDetails {

  private def isValidDate(value: String): Boolean = DatePattern.matches(value)

  private val validDate: Reads[String] = Reads.verifying[String](isValidDate)

  implicit val reads: Reads[UniversalCreditLiabilityDetails] = (
    (JsPath \ "universalCreditRecordType").read[UniversalCreditRecordType] and
      (JsPath \ "dateOfBirth").read(validDate) and
      (JsPath \ "liabilityStartDate").read(validDate) and
      (JsPath \ "liabilityEndDate").readNullable(validDate)
  )(UniversalCreditLiabilityDetails.apply _)

  implicit val writes: OWrites[UniversalCreditLiabilityDetails] = Json.writes[UniversalCreditLiabilityDetails]
}
