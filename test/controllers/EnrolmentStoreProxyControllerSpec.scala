/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers

import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderNames

class EnrolmentStoreProxyControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  private val authHeader: (String, String) = HeaderNames.authorisation -> "token"

  "GET " - {

    "must return FORBIDDEN response when 'Authorization' header is missing in the input request" in {
      val mdrService = "HMRC-CBC-ORG~CBCID~7777"
      val request    = FakeRequest(GET, routes.EnrolmentStoreProxyController.status(mdrService).url)
      val result     = route(app, request).value
      status(result) shouldBe FORBIDDEN
    }

    "must return Ok response with groupid" in {

      val mdrService = "HMRC-CBC-ORG~CBCID~XACBC0000123777"
      val request    = FakeRequest(GET, routes.EnrolmentStoreProxyController.status(mdrService).url).withHeaders(authHeader)
      val result     = route(app, request).value

      val expectedJson =
        Json.parse("""{"principalGroupIds":["ABCEDEFGI1234567","ABCEDEFGI1234568"],"delegatedGroupIds":["ABCEDEFGI1234567","ABCEDEFGI1234568"]}""".stripMargin)

      status(result) shouldBe OK
      contentAsJson(result) shouldBe expectedJson

    }

    "must return Ok response with no groupid" in {

      val mdrService = "HMRC-CBC-ORG~CBCID~XACBC0000123888"
      val request    = FakeRequest(GET, routes.EnrolmentStoreProxyController.status(mdrService).url).withHeaders(authHeader)
      val result     = route(app, request).value
      val expectedJson =
        Json.parse("""{}""".stripMargin)
      status(result) shouldBe OK
      contentAsJson(result) shouldBe expectedJson
    }

    "must return NoContent response" in {

      val mdrService = "HMRC-MDR-ORG~MDRID~XAMDR0000123456"
      val request    = FakeRequest(GET, routes.EnrolmentStoreProxyController.status(mdrService).url).withHeaders(authHeader)
      val result     = route(app, request).value
      status(result) shouldBe NO_CONTENT
    }
  }
}
