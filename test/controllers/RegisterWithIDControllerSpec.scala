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
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, route, status, POST}
import uk.gov.hmrc.http.HeaderNames

class RegisterWithIDControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  private val authHeader: (String, String) = HeaderNames.authorisation -> "token"

  private val utrAndStatus: Seq[(String, Int)] = Seq(
    ("9999999990", INTERNAL_SERVER_ERROR),
    ("9999999991", BAD_REQUEST),
    ("9999999992", SERVICE_UNAVAILABLE),
    ("9999999993", SERVICE_UNAVAILABLE),
    ("9999999994", NOT_FOUND)
  )

  "POST " - {

    "must return FORBIDDEN response when 'Authorization' header is missing in the input request" in {
      val json: JsValue = Json.obj()

      val request = FakeRequest(POST, routes.RegisterWithIDController.register.url).withBody(json)
      val result  = route(app, request).value

      status(result) shouldBe FORBIDDEN
    }

    for ((idNumber, errorStatus) <- utrAndStatus)
      s"return $errorStatus for invalid registerWithIDRequest for the UTR value $idNumber" in {
        val jsonPayload: String =
          s"""
             |{
             |  "registerWithIDRequest": {
             |    "requestDetail": {
             |      "IDType": "UTR",
             |      "IDNumber": "$idNumber",
             |      "organisation": {
             |          "organisationName": "cbc company"
             |       }
             |    }
             |  }
             |}""".stripMargin
        val json: JsValue = Json.parse(jsonPayload)

        val request = FakeRequest(POST, routes.RegisterWithIDController.register.url).withBody(json).withHeaders(authHeader)
        val result  = route(app, request).value

        status(result) shouldBe errorStatus
      }

    "must return Ok response and valid registerWithIDRequest for an Organisation" in {
      val jsonPayload: String = s"""
                                   |{
                                   |  "registerWithIDRequest": {
                                   |    "requestDetail": {
                                   |      "IDType": "UTR",
                                   |      "IDNumber": "1234567890",
                                   |       "organisation": {
                                   |          "organisationName": "cbc company"
                                   |       }
                                   |    }
                                   |  }
                                   |}""".stripMargin
      val json: JsValue = Json.parse(jsonPayload)

      val request = FakeRequest(POST, routes.RegisterWithIDController.register.url).withBody(json).withHeaders(authHeader)
      val result  = route(app, request).value

      status(result) shouldBe OK
    }

    "must return Ok response and valid registerWithIDRequest for another Organisation" in {
      val jsonPayload: String = s"""
                                   |{
                                   |  "registerWithIDRequest": {
                                   |    "requestDetail": {
                                   |      "IDType": "UTR",
                                   |      "IDNumber": "1234567890",
                                   |       "organisation": {
                                   |          "organisationName": "second company"
                                   |       }
                                   |    }
                                   |  }
                                   |}""".stripMargin
      val json: JsValue = Json.parse(jsonPayload)

      val request = FakeRequest(POST, routes.RegisterWithIDController.register.url).withBody(json).withHeaders(authHeader)
      val result  = route(app, request).value

      status(result) shouldBe OK
    }

    "must return BadRequest for an invalid input" in {
      val jsonPayload: String = s"""
                                   |{
                                   |  "registerWithIDRequest": {
                                   |    "requestDetail": {
                                   |      "IDType": "TEST",
                                   |      "IDNumber": "validNumber",
                                   |       "organisation": {
                                   |          "organisationName": "cbc company"
                                   |       }
                                   |    }
                                   |  }
                                   |}""".stripMargin
      val json: JsValue = Json.parse(jsonPayload)

      val request = FakeRequest(POST, routes.RegisterWithIDController.register.url).withBody(json).withHeaders(authHeader)
      val result  = route(app, request).value

      status(result) shouldBe NOT_FOUND
    }
  }
}
