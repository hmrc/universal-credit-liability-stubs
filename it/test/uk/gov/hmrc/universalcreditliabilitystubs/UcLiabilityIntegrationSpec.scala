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

package uk.gov.hmrc.universalcreditliabilitystubs

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient

class UcLiabilityIntegrationSpec
  extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite {

  private given WSClient = app.injector.instanceOf[WSClient]

  "UC Liability endpoint" must {
    "respond with 204 status" in {
      val response =
        wsUrl(s"/universal-credit-liability-stubs/person/nino/liability/universal-credit")
          .execute("POST")
          .futureValue

      response.status mustBe 204
    }
  }
}