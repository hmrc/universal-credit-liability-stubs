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
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.DefaultBodyWritables.*
import play.api.libs.ws.{WSClient, WSResponse}
import scala.util.Random

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

  def callApi(body: String,headers: (String, String)*): WSResponse  = {
    val randomNino: String = "AB%06d".format (Random.nextInt (999999) )
    wsClient
      .url(s"$baseUrl/universal-credit-liability-stubs/person/$randomNino/liability/universal-credit")
      .addHttpHeaders(headers: _*)
      .post(body)
      .futureValue
  }

  "UC Liability endpoint" must {
    "respond with 204 status" in {

      val response = callApi(body = "")
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability Insert LCW/LCWRA no end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19"
        }"""

      val response = callApi(payload, ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability Insert LCW/LCWRA with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability - Insert UC (S2P) no end date" must {
    "respond with 204 status" in {

      val payload =
        """{
             "universalCreditRecordType": "UC (S2P)",
             "universalCreditAction": "Insert",
             "dateOfBirth": "2002-10-10",
             "liabilityStartDate": "2025-08-19"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability - Insert UC (S2P) with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "UC (S2P)",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
         }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability - Insert UC no end date" must {
    "respond with 204 status" in {

      val payload =
        """{
             "universalCreditRecordType": "UC",
             "universalCreditAction": "Insert",
             "dateOfBirth": "2002-10-10",
             "liabilityStartDate": "2025-08-19"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type","application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability - Insert UC with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "UC",
            "universalCreditAction": "Insert",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability Terminate LCW/LCWRA with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "LCW/LCWRA",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability Terminate UC (S2P) with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "UC (S2P)",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }

  "UC Liability Terminate UC with end date" must {
    "respond with 204 status" in {

      val payload =
        """{
            "universalCreditRecordType": "UC",
            "universalCreditAction": "Terminate",
            "dateOfBirth": "2002-10-10",
            "liabilityStartDate": "2025-08-19",
            "liabilityEndDate": "2025-01-04"
        }"""

      val response = callApi(body = payload, headers = ("Content-Type", "application/json"))
      response.status mustBe Status.NO_CONTENT
    }
  }
}
