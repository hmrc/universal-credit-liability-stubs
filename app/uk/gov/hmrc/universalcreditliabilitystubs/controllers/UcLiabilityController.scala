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
import uk.gov.hmrc.universalcreditliabilitystubs.config.AppConfig
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.{Failure, Failures}
import uk.gov.hmrc.universalcreditliabilitystubs.services.{MappingService, SchemaValidationService}
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.ErrorCodes.{ForbiddenCode, InvalidAuth}
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.{ForbiddenReason, InvalidAuthReason, ValidationPatterns}
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames.{Authorization, GovUkOriginatorId}

import java.util.Base64
import javax.inject.Inject

@Singleton
class UcLiabilityController @Inject() (
  cc: ControllerComponents,
  schemaValidationService: SchemaValidationService,
  mappingService: MappingService,
  appConfig: AppConfig
) extends BackendController(cc) {

  def insertLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    (for {
      _ <- validateAuthorization(request)
      _ <- validateGovUkOriginatorId(request)
      _ <- schemaValidationService.validateInsertLiabilityRequest(request, nino)
    } yield mappingService.mapSystemErrors(nino).getOrElse {
      mappingService.map422ErrorResponses(nino) match {
        case Some(failure) => UnprocessableEntity(Json.toJson(Failures(Seq(failure))))
        case None          => NoContent
      }
    }).merge
  }

  def terminateLiabilityDetails(nino: String): Action[JsValue] = Action(parse.json) { request =>
    (for {
      _ <- validateAuthorization(request)
      _ <- validateGovUkOriginatorId(request)
      _ <- schemaValidationService.validateTerminateLiabilityRequest(request, nino)
    } yield mappingService.mapSystemErrors(nino).getOrElse {
      mappingService.map422ErrorResponses(nino) match {
        case Some(failure) => UnprocessableEntity(Json.toJson(Failures(Seq(failure))))
        case None          => NoContent
      }
    }).merge
  }

  private def validateAuthorization[T](request: Request[T]): Either[Result, String] = {
    val credentials: String  = s"${appConfig.hipClientId}:${appConfig.hipClientSecret}"
    val encoded: String      = Base64.getEncoder.encodeToString(credentials.getBytes("UTF-8"))
    val expectedAuth: String = s"Basic $encoded"

    request.headers
      .get(Authorization)
      .filter(_ == expectedAuth)
      .toRight(
        Unauthorized(Json.toJson(Failure(reason = InvalidAuthReason, code = InvalidAuth)))
      )
  }

  def validateGovUkOriginatorId[T](request: Request[T]): Either[Result, String] =
    request.headers
      .get(GovUkOriginatorId)
      .filter(originatorId => ValidationPatterns.isValidGovUkOriginatorId(originatorId))
      .toRight(Forbidden(Json.toJson(Failure(reason = ForbiddenReason, code = ForbiddenCode))))
}
