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
import play.api.libs.json.{JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HeaderNames

class SubscriptionControllerSpec extends AnyFreeSpec with Matchers with GuiceOneAppPerSuite with OptionValues {

  "POST " - {
    "createSubscription" - {
      "must return FORBIDDEN response when 'Authorization' header is missing in the input request" in {
        val json: JsValue = Json.obj()

        val request = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json)
        val result  = route(app, request).value

        status(result) shouldBe FORBIDDEN
      }

      "must return OK response for the valid input request" in {

        val input: String =
          """
            |{
            | "createSubscriptionForCBCRequest": {
            |  "requestCommon": {
            |   "regime": "CBC",
            |   "receiptDate": "2020-09-12T18:03:45Z",
            |   "acknowledgementReference": "abcdefghijklmnopqrstuvwxyz123456",
            |   "originatingSystem": "MDTP"
            |  },
            |  "requestDetail": {
            |   "IDType": "SAFE",
            |   "IDNumber": "AB123456Z",
            |   "tradingName": "Tools for Traders Limited",
            |   "isGBUser": true,
            |   "primaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "john@toolsfortraders.com",
            |    "phone": "0188899999",
            |    "mobile": "07321012345"
            |   },
            |   "secondaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "contact@toolsfortraders.com",
            |    "phone": "+44 020 39898980"
            |   }
            |  }
            | }
            |}
            |""".stripMargin

        val json: JsValue                = Json.parse(input)
        val authHeader: (String, String) = HeaderNames.authorisation -> "token"
        val request                      = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json).withHeaders(authHeader)
        val result                       = route(app, request).value

        status(result) shouldBe OK
      }

      "must return Conflict response for the valid input request" in {

        val input: String =
          """
            |{
            | "createSubscriptionForCBCRequest": {
            |  "requestCommon": {
            |   "regime": "CBC",
            |   "receiptDate": "2020-09-12T18:03:45Z",
            |   "acknowledgementReference": "abcdefghijklmnopqrstuvwxyz123456",
            |   "originatingSystem": "MDTP"
            |  },
            |  "requestDetail": {
            |   "IDType": "SAFE",
            |   "IDNumber": "AB123456Z",
            |   "tradingName": "Tools for Traders Limited",
            |   "isGBUser": true,
            |   "primaryContact": {
            |    "organisation": {
            |     "organisationName": "duplicate"
            |    },
            |    "email": "john@toolsfortraders.com",
            |    "phone": "0188899999",
            |    "mobile": "07321012345"
            |   },
            |   "secondaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "contact@toolsfortraders.com",
            |    "phone": "+44 020 39898980"
            |   }
            |  }
            | }
            |}
            |""".stripMargin

        val json: JsValue                = Json.parse(input)
        val authHeader: (String, String) = HeaderNames.authorisation -> "token"
        val request                      = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json).withHeaders(authHeader)
        val result                       = route(app, request).value

        status(result) shouldBe CONFLICT
      }

      "must return NotFound response for the valid input request" in {

        val input: String =
          """
            |{
            | "createSubscriptionForCBCRequest": {
            |  "requestCommon": {
            |   "regime": "CBC",
            |   "receiptDate": "2020-09-12T18:03:45Z",
            |   "acknowledgementReference": "abcdefghijklmnopqrstuvwxyz123456",
            |   "originatingSystem": "MDTP"
            |  },
            |  "requestDetail": {
            |   "IDType": "SAFE",
            |   "IDNumber": "AB123456Z",
            |   "tradingName": "Tools for Traders Limited",
            |   "isGBUser": true,
            |   "primaryContact": {
            |    "organisation": {
            |     "organisationName": "notFound"
            |    },
            |    "email": "john@toolsfortraders.com",
            |    "phone": "0188899999",
            |    "mobile": "07321012345"
            |   },
            |   "secondaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "contact@toolsfortraders.com",
            |    "phone": "+44 020 39898980"
            |   }
            |  }
            | }
            |}
            |""".stripMargin

        val json: JsValue                = Json.parse(input)
        val authHeader: (String, String) = HeaderNames.authorisation -> "token"
        val request                      = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json).withHeaders(authHeader)
        val result                       = route(app, request).value

        status(result) shouldBe NOT_FOUND
      }

      "must return BadRequest response for the invalid input request" in {

        val input: String =
          """
            |{
            | "createSubscriptionForCBCRequest": {
            |  "requestCommon": {
            |   "regime": "XYZ",
            |   "receiptDate": "2020-09-12T18:03:45Z",
            |   "acknowledgementReference": "abcdefghijklmnopqrstuvwxyz123456",
            |   "originatingSystem": "MDTP"
            |  },
            |  "requestDetail": {
            |   "IDType": "SAFE",
            |   "IDNumber": "AB123456Z",
            |   "tradingName": "Tools for Traders Limited",
            |   "isGBUser": true,
            |   "primaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "john@toolsfortraders.com",
            |    "phone": "0188899999",
            |    "mobile": "07321012345"
            |   },
            |   "secondaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "contact@toolsfortraders.com",
            |    "phone": "+44 020 39898980"
            |   }
            |  }
            | }
            |}
            |""".stripMargin

        val json: JsValue                = Json.parse(input)
        val authHeader: (String, String) = HeaderNames.authorisation -> "token"
        val request                      = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json).withHeaders(authHeader)
        val result                       = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }

      "must return BadRequest response for the input json missing mandatory field regime" in {

        val input: String =
          """
            |{
            | "createSubscriptionForCBCRequest": {
            |  "requestCommon": {
            |   "receiptDate": "2020-09-12T18:03:45Z",
            |   "acknowledgementReference": "abcdefghijklmnopqrstuvwxyz123456",
            |   "originatingSystem": "MDTP"
            |  },
            |  "requestDetail": {
            |   "IDType": "SAFE",
            |   "IDNumber": "AB123456Z",
            |   "tradingName": "Tools for Traders Limited",
            |   "isGBUser": true,
            |   "primaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "john@toolsfortraders.com",
            |    "phone": "0188899999",
            |    "mobile": "07321012345"
            |   },
            |   "secondaryContact": {
            |    "organisation": {
            |     "organisationName": "Tools for Traders"
            |    },
            |    "email": "contact@toolsfortraders.com",
            |    "phone": "+44 020 39898980"
            |   }
            |  }
            | }
            |}
            |""".stripMargin

        val json: JsValue                = Json.parse(input)
        val authHeader: (String, String) = HeaderNames.authorisation -> "token"
        val request                      = FakeRequest(POST, routes.SubscriptionController.createSubscription.url).withBody(json).withHeaders(authHeader)
        val result                       = route(app, request).value

        status(result) shouldBe BAD_REQUEST
      }
    }

    "readSubscription" - {
      "submit and return response OK for valid input" in {
        val jsonPayload: String =
          s"""
             |{
             |  "displaySubscriptionForCBCRequest": {
             |    "requestDetail": {
             |      "IDType": "CBC",
             |      "IDNumber": "XACBC0000123778"
             |    }
             |  }
             |}""".stripMargin
        val json: JsValue = Json.parse(jsonPayload)

        val request = FakeRequest(POST, routes.SubscriptionController.readSubscription().url).withBody(json)
        val result  = route(app, request).value

        status(result) shouldBe OK
      }

      "submit and return response ServiceUnavailable for invalid input" in {
        val jsonPayload: String =
          s"""
             |{
             |  "displaySubscriptionForCBCRequest": {
             |    "requestDetail": {
             |      "IDType": "CBC",
             |      "IDNumber": "XE0000123456777"
             |    }
             |  }
             |}""".stripMargin
        val json: JsValue = Json.parse(jsonPayload)

        val request = FakeRequest(POST, routes.SubscriptionController.readSubscription().url).withBody(json)
        val result  = route(app, request).value

        status(result) shouldBe SERVICE_UNAVAILABLE
      }
    }

  }
}
