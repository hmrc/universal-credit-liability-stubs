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

package uk.gov.hmrc.universalcreditliabilitystubs.helpers

import com.atlassian.oai.validator.OpenApiInteractionValidator
import com.atlassian.oai.validator.model.{SimpleRequest, SimpleResponse}
import com.atlassian.oai.validator.report.ValidationReport.Level.IGNORE
import com.atlassian.oai.validator.report.{LevelResolver, ValidationReport}
import play.api.libs.ws.{EmptyBody, InMemoryBody, WSRequest, WSResponse}
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import com.atlassian.oai.validator.model.Request.Method

import scala.collection.convert.AsScalaExtensions
import scala.collection.convert.AsJavaExtensions
import scala.io.Source

trait OpenApiValidatorHelper extends AsScalaExtensions with AsJavaExtensions {

  private val validator: OpenApiInteractionValidator = OpenApiInteractionValidator
    .createForInlineApiSpecification(Source.fromResource(OpenApiValidatorHelper.openApiResource).mkString)
    .withLevelResolver(
      // The key here is to use the level resolver to ignore the response validation messages
      // Without this they would be emitted at ERROR level and cause a validation failure.
      LevelResolver
        .create()
        .withLevel("validation.response", IGNORE)
        .build()
    )
    .build()

  def openApiPathValidatorFor(wsRequest: WSRequest): OpenApiPathValidator =
    new OpenApiPathValidator(wsRequest, validator)

}

object OpenApiValidatorHelper {
  val openApiResource: String = "openapi.hip.jf18645.2.0.1.yaml"
}

class OpenApiPathValidator private[helpers](wsRequest: WSRequest, validator: OpenApiInteractionValidator)
    extends AsJavaExtensions {

  def validateRequest: ValidationReport =
    validator.validateRequest(buildRequest(wsRequest))

  def validateResponse(wsResponse: WSResponse): ValidationReport =
    validator.validateResponse(
      wsRequest.uri.getPath,
      wsRequest.method match {
        case "POST" => Method.POST
        case method => throw new Error(s"Unsupported method: $method")
      },
      buildResponse(wsResponse)
    )

  private def buildRequest(wsRequest: WSRequest): SimpleRequest = {
    val initBuilder = SimpleRequest.Builder
      .post(wsRequest.uri.getPath)

    val builderWithHeaders = wsRequest.headers.toSeq
      .foldLeft(
        initBuilder
      ) { case (builder, (header, value)) =>
        builder.withHeader(header, value: _*)
      }

    val requestBodyAsString: String = wsRequest.body match {
      case InMemoryBody(bytes) => bytes.decodeString("utf-8")
      case EmptyBody           => ""
      case _                   => throw Error("Unsupported WsBody type")
    }

    val buildWithBody = builderWithHeaders.withBody(
      requestBodyAsString
    )

    buildWithBody.build()
  }

  private def buildResponse(wsResponse: WSResponse) = {
    val baseBuilder = SimpleResponse.Builder.status(wsResponse.status)

    val builderWithHeaders = wsResponse.headers.toSeq
      .foldLeft(
        baseBuilder
      ) { case (builder, (header, value)) =>
        builder.withHeader(header, value.toList.asJava)
      }

    builderWithHeaders.withBody(wsResponse.body[String]).build()
  }
}
