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

import cats.data.{EitherNec, NonEmptyChain}
import cats.syntax.all.*
import play.api.Logger
import play.api.libs.json.*
import play.api.mvc.Results.BadRequest
import play.api.mvc.{Request, Result}
import uk.gov.hmrc.universalcreditliabilitystubs.models.errors.*
import uk.gov.hmrc.universalcreditliabilitystubs.models.request.{InsertLiabilityRequest, TerminateLiabilityRequest}
import uk.gov.hmrc.universalcreditliabilitystubs.services.SchemaValidationService.*
import uk.gov.hmrc.universalcreditliabilitystubs.utils.ApplicationConstants.PathParameter.Nino
import uk.gov.hmrc.universalcreditliabilitystubs.utils.{ApplicationConstants, HeaderNames}

import scala.util.matching.Regex

class SchemaValidationService {

  def validateInsertLiabilityRequest(request: Request[JsValue], nino: String): Either[Result, InsertLiabilityRequest] =
    (
      validateCorrelationId(request.headers.get(HeaderNames.CorrelationId)),
      validateNino(nino),
      validateJson[InsertLiabilityRequest](request)
    ).parMapN((_, _, submitLiabilityRequest) => submitLiabilityRequest)
      .leftMap { necOfFailures =>
        mergeFailures(necOfFailures)
      }

  def validateTerminateLiabilityRequest(
    request: Request[JsValue],
    nino: String
  ): Either[Result, TerminateLiabilityRequest] =
    (
      validateCorrelationId(request.headers.get(HeaderNames.CorrelationId)),
      validateNino(nino),
      validateJson[TerminateLiabilityRequest](request)
    ).parMapN((_, _, terminateLiabilityRequest) => terminateLiabilityRequest)
      .leftMap { necOfFailures =>
        mergeFailures(necOfFailures)
      }

  private def validateCorrelationId(correlationId: Option[String]): EitherNec[Failures, Unit] =
    correlationId match {
      case Some(id) if CorrelationIdPattern.matches(id) => Right(())
      case _                                            =>
        Left(
          NonEmptyChain.one(
            Failures(
              failures = Seq(
                ApplicationConstants.invalidInputFailure(HeaderNames.CorrelationId)
              )
            )
          )
        )
    }

  private def validateNino(nino: String): EitherNec[Failures, Unit] =
    NinoPattern.findFirstMatchIn(nino) match {
      case Some(_) => Right(())
      case _       =>
        Left(
          NonEmptyChain.one(
            Failures(
              failures = Seq(
                ApplicationConstants.invalidInputFailure(Nino)
              )
            )
          )
        )
    }

  private def validateJson[T](request: Request[JsValue])(implicit reads: Reads[T]): EitherNec[Failures, T] =
    request.body.validate[T] match {
      case JsSuccess(validatedRequest, _) =>
        Right(validatedRequest)

      case JsError(errors) =>
        val failures = errors.flatMap { case (path, validationErrors) =>
          val field = path.toString().stripPrefix("/")
          validationErrors.map { _ =>
            ApplicationConstants.invalidInputFailure(field)
          }
        }.toSeq

        Left(
          NonEmptyChain.one(
            Failures(failures)
          )
        )
    }

  private def mergeFailures(failures: NonEmptyChain[Failures]): Result = {
    val allFailures: Seq[Failure] = failures.toList.flatMap(_.failures)
    Logger(this.getClass).warn(Json.stringify(Json.toJson(Failures(allFailures))))
    BadRequest
  }
}

object SchemaValidationService {
  val CorrelationIdPattern: Regex =
    "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$".r

  private val NinoPattern: Regex =
    "^([ACEHJLMOPRSWXY][A-CEGHJ-NPR-TW-Z]|B[A-CEHJ-NPR-TW-Z]|G[ACEGHJ-NPR-TW-Z]|[KT][A-CEGHJ-MPR-TW-Z]|N[A-CEGHJL-NPR-SW-Z]|Z[A-CEGHJ-NPR-TW-Y])[0-9]{6}$".r
}
