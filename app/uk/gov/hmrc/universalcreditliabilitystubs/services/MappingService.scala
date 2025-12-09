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

import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure

class MappingService {

  def map422ErrorResponses(nino: String): Option[Failure] =
    nino.take(4) match {
      case "AA01" => Some(Failure("Start Date and End Date must be earlier than Date of Death", "55006"))
      case "AA02" => Some(Failure("End Date must be earlier than State Pension Age", "55008"))
      case "AA03" => Some(Failure("End Date later than Date of Death", "55027"))
      case "AA04" => Some(Failure("Start Date later than SPA", "55029"))
      case "AA05" => Some(Failure("A conflicting or identical Liability is already recorded", "55038"))
      case "AA06" => Some(Failure("NO corresponding liability found", "55039"))
      case "AA07" => Some(Failure("Start Date is not before date of death", "64996"))
      case "AA08" => Some(Failure("LCW/LCWRA not within a period of UC", "64997"))
      case "AA09" => Some(Failure("LCW/LCWRA Override not within a period of LCW/LCWRA", "64998"))
      case "AA10" => Some(Failure("Start date must not be before 16th birthday", "65026"))
      case "AA11" => Some(Failure("Start date before 29/04/2013", "65536"))
      case "AA12" => Some(Failure("End date before start date", "65537"))
      case "AA13" => Some(Failure("The NINO input matches a Pseudo Account", "65541"))
      case "AA14" => Some(Failure("The NINO input matches a non-live account (including redundant, amalgamated and administrative account types)", "65542"))
      case "AA15" => Some(Failure("The NINO input matches an account that has been transferred to the Isle of Man", "65543"))
      case "AA16" => Some(Failure("Start Date after Death", "99999"))
      case _      => None
    }

}
