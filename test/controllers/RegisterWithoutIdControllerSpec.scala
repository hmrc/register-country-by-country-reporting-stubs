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

class RegisterWithoutIdControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  private val authHeader: (String, String) = HeaderNames.authorisation -> "token"

  private val nameAndErrorStatus: Seq[(String, Int)] = Seq(
    ("error", INTERNAL_SERVER_ERROR),
    ("invalid", BAD_REQUEST),
    ("server", SERVICE_UNAVAILABLE),
    ("notProcessed", SERVICE_UNAVAILABLE),
    ("notFound", NOT_FOUND)
  )

  "RegisterWithoutIdController" - {

    "must return FORBIDDEN response when 'Authorization' header is missing in the input request" in {
      val json: JsValue = Json.obj()

      val request = FakeRequest(POST, routes.RegisterWithoutIdController.register.url).withBody(json)
      val result  = route(app, request).value

      status(result) shouldBe FORBIDDEN
    }

    for ((name, errorStatus) <- nameAndErrorStatus)
      s"must return $errorStatus for invalid registerWithoutIDRequest with organisation name as $name" in {
        val jsonPayload: String = s"""
                                     |{
                                     |  "registerWithoutIDRequest": {
                                     |  "requestCommon":{
                                     |      "regime": "CBC"
                                     |   },
                                     |    "requestDetail": {
                                     |       "organisation": {
                                     |          "organisationName": "$name"
                                     |       }
                                     |    }
                                     |  }
                                     |}""".stripMargin
        val json: JsValue = Json.parse(jsonPayload)

        val request = FakeRequest(POST, routes.RegisterWithoutIdController.register.url).withBody(json).withHeaders(authHeader)
        val result  = route(app, request).value

        status(result) shouldBe errorStatus
      }

    "must return Ok response and valid registerWithoutIDRequest for an Organisation" in {
      val jsonPayload: String = s"""
                                   |{
                                   |  "registerWithoutIDRequest": {
                                   |    "requestCommon":{
                                   |      "regime": "CBC"
                                   |   },
                                   |    "requestDetail": {
                                   |       "organisation": {
                                   |          "organisationName": "Some Company"
                                   |       }
                                   |    }
                                   |  }
                                   |}""".stripMargin
      val json: JsValue = Json.parse(jsonPayload)

      val request = FakeRequest(POST, routes.RegisterWithoutIdController.register.url).withBody(json).withHeaders(authHeader)
      val result  = route(app, request).value

      status(result) shouldBe OK
    }

    "must return BadRequest for an invalid input (invalid regime)" in {
      val jsonPayload: String = s"""
                                   |{
                                   |  "registerWithoutIDRequest": {
                                   |    "requestCommon":{
                                   |      "regime": "XYZ"
                                   |   },
                                   |    "requestDetail": {
                                   |"organisation": {
                                   |          "organisationName": "Some Company"
                                   |       }
                                   |    }
                                   |  }
                                   |}""".stripMargin
      val json: JsValue = Json.parse(jsonPayload)

      val request = FakeRequest(POST, routes.RegisterWithoutIdController.register.url).withBody(json).withHeaders(authHeader)
      val result  = route(app, request).value

      status(result) shouldBe BAD_REQUEST
    }

  }
}
