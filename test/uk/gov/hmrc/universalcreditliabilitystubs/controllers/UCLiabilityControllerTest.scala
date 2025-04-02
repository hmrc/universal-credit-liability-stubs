package uk.gov.hmrc.universalcreditliabilitystubs.controllers

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.http.Status
import play.api.test.Helpers.*
import play.api.test.{FakeRequest, Helpers}

class UCLiabilityControllerTest extends AnyWordSpec with Matchers {

  private val fakeRequest = FakeRequest("POST", "/")
  private val controller  = new UCLiabilityController(Helpers.stubControllerComponents())

  "POST /" should {
    "return 204" in {
      val result = controller.submitLiabilityDetails("nino")(fakeRequest)
      status(result) shouldBe Status.NO_CONTENT
    }
  }
}
