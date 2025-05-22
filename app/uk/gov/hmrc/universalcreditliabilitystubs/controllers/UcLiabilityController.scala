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

package uk.gov.hmrc.universalcreditliabilitystubs.controllers

import jakarta.inject.Singleton
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.*
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.Failure
import uk.gov.hmrc.universalcreditliabilitystubs.services.UcLiabilityService
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ErrorCodes.ForbiddenCode
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ForbiddenReason
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames.OriginatorId

import javax.inject.Inject

@Singleton
class UcLiabilityController @Inject() (cc: ControllerComponents, ucLiabilityService: UcLiabilityService)
    extends BackendController(cc) {

  def insertLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    request.headers.get(OriginatorId) match {
      case Some(govUkOriginatorId) =>
        // Need to determine what the OriginatorId will be and set it in config
        ucLiabilityService.validateInsertLiabilityRequest(request, nino) match {
          case Right(terminateRequest) =>
            // Returning a 204 here pending when business discussions on business logic is finalized
            NoContent

          case Left(errorResult) =>
            errorResult
        }

      case None =>
        Forbidden(Json.toJson(Failure(reason = ForbiddenReason, code = ForbiddenCode)))
    }
  }

  def terminateLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    request.headers.get(OriginatorId) match {
      case Some(govUkOriginatorId) =>
        // Need to determine what the OriginatorId will be and set it in config
        ucLiabilityService.validateTerminateLiabilityRequest(request, nino) match {
          case Right(terminateRequest) =>
            // Returning a 204 here pending when business discussions on business logic is finalized
            NoContent

          case Left(errorResult) =>
            errorResult
        }

      case None =>
        Forbidden(Json.toJson(Failure(reason = ForbiddenReason, code = ForbiddenCode)))
    }
  }
}
