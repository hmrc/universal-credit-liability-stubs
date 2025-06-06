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

package uk.gov.hmrc.universalcreditliabilitystubs.filters

import jakarta.inject.Inject
import org.apache.pekko.stream.Materializer
import play.api.mvc.*
import uk.gov.hmrc.universalcreditliabilitystubs.services.SchemaValidationService.CorrelationIdPattern
import uk.gov.hmrc.universalcreditliabilitystubs.utils.HeaderNames

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

class CorrelationIdFilter @Inject() (implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val optionalCorrelationId = request.headers.get(HeaderNames.CorrelationId)

    val finalCorrelationId = optionalCorrelationId match {
      case Some(id) if CorrelationIdPattern.matches(id) => id
      case _                                            => UUID.randomUUID().toString
    }

    nextFilter(request).map { result =>
      result.withHeaders(HeaderNames.CorrelationId -> finalCorrelationId)
    }
  }
}
