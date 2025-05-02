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
import org.scalatest.matchers.must.Matchers.mustBe
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.libs.ws.DefaultBodyWritables.*

class UcLiabilityIntegrationSpec
    extends PlaySpec
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite {

  private val wsClient = app.injector.instanceOf[WSClient]
  private val baseUrl  = s"http://localhost:$port"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .build()

  "UC Liability endpoint" must {
    "respond with 204 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
          .execute("POST")
          .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability Insert LCW/LCWRA no end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19"
      }""".stripMargin

      val response = wsClient
          .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
          .withHttpHeaders(("Content-Type", "application/json"))
          .post(payload)
          .futureValue
          
      response.status mustBe 204
    }
  }

  "UC Liability Insert LCW/LCWRA with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability - Insert UC (S2P) no end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
             "universalCreditRecordType": "UC (S2P)",
             "universalCreditAction": "Insert",
             "dateOfBirth": "2002-10-10",
             "liabilityStartDate": "2025-08-19"
       }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability - Insert UC (S2P) with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "UC (S2P)",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
         }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability - Insert UC no end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
             "universalCreditRecordType": "UC",
             "universalCreditAction": "Insert",
             "dateOfBirth": "2002-10-10",
             "liabilityStartDate": "2025-08-19"
       }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability - Insert UC with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "UC",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
         }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }


  "UC Liability Terminate LCW/LCWRA with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
          }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability Terminate UC (S2P) with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "UC (S2P)",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
          }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }

  "UC Liability Terminate UC with end date" must {
    "respond with 204 status" in {

      val payload =
        s"""{
            "universalCreditRecordType": "UC",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
          }""".stripMargin

      val response = wsClient
        .url(s"$baseUrl/universal-credit-liability-stubs/person/nino/liability/universal-credit")
        .withHttpHeaders(("Content-Type", "application/json"))
        .post(payload)
        .futureValue

      response.status mustBe 204
    }
  }
}