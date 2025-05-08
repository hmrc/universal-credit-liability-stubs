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

package uk.gov.hmrc.universalcreditliabilitystubs.utils

import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure

object ApplicationConstants {
  val Nino = "nino"

  def invalidInputFailure(field: String): Failure =
    Failure(
      reason = ErrorMessages.invalidInput(field),
      code = ErrorCodes.InvalidInput
    )

  private object ErrorCodes {
    val InvalidInput = "400.1"
  }

  private object ErrorMessages {
    def invalidInput(field: String): String =
      s"Constraint Violation - Invalid/Missing input parameter: $field"
  }
}

object HeaderNames {
  val CorrelationId = "correlationId"
}
