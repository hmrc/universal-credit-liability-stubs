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
import uk.gov.hmrc.universalcreditliabilitystubs.services.SchemaValidationService
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ErrorCodes.ForbiddenCode
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ForbiddenReason
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames.OriginatorId

import javax.inject.Inject

@Singleton
class UcLiabilityController @Inject() (cc: ControllerComponents, ucLiabilityService: SchemaValidationService)
    extends BackendController(cc) {

  def insertLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    (for {
      _      <- validateOriginatorId(request)
      result <- ucLiabilityService.validateInsertLiabilityRequest(request, nino)
    } yield NoContent).merge
  }

  def terminateLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    (for {
      _      <- validateOriginatorId(request)
      result <- ucLiabilityService.validateTerminateLiabilityRequest(request, nino)
    } yield NoContent).merge
  }

  private def validateOriginatorId[T](request: Request[T]) =
    request.headers
      .get(OriginatorId)
      .filter(_ => true)
      .toRight(Forbidden(Json.toJson(Failure(reason = ForbiddenReason, code = ForbiddenCode))))
}
