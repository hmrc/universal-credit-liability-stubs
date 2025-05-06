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

import play.api.libs.json.Json.toJson
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc.Results.BadRequest
import play.api.mvc.{Request, Result}
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.*
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.SubmitLiabilityRequest

import scala.util.matching.Regex

class UcLiabilityService {

  def validateRequest(request: Request[JsValue], nino: String): Either[Result, SubmitLiabilityRequest] =
    for {
      _                      <- validateCorrelationId(request.headers.get("correlationId"))
      _                      <- validateNino(nino)
      submitLiabilityRequest <- validateJson(request)
    } yield submitLiabilityRequest

  private def validateCorrelationId(correlationId: Option[String]): Either[Result, Unit] = {
    val uuidPattern: Regex =
      "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$".r

    correlationId match {
      case Some(id) if uuidPattern.matches(id) => Right(())
      case _                                   =>
        Left(
          BadRequest(
            toJson(
              Failures(
                failures = Seq(
                  Failure(
                    reason = "Constraint Violation - Invalid/Missing input parameter: correlationId",
                    code = "400.1"
                  )
                )
              )
            )
          )
        )
    }
  }

  private def validateNino(nino: String): Either[Result, Unit] = {

    val ninoPattern: Regex =
      "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}$".r

    ninoPattern.findFirstMatchIn(nino) match {
      case Some(_) => Right(())
      case _       =>
        Left(
          BadRequest(
            toJson(
              Failures(
                failures =
                  Seq(Failure(reason = "Constraint Violation - Invalid/Missing input parameter: nino", code = "400.1"))
              )
            )
          )
        )
    }
  }

  private def validateJson(request: Request[JsValue]): Either[Result, SubmitLiabilityRequest] =
    request.body.validate[SubmitLiabilityRequest] match {
      case JsSuccess(validatedRequest, _) =>
        Right(validatedRequest)

      case JsError(errors) =>
        val failures = errors.flatMap { case (path, validationErrors) =>
          val field = path.toString().stripPrefix("/")
          validationErrors.map { err =>
            Failure(
              reason = s"Constraint Violation - Invalid/Missing input parameter: $field",
              code = "400.1"
            )
          }
        }.toSeq

        Left(BadRequest(Json.toJson(Failures(failures))))
    }
}
