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

import controllers.actions.AuthActionFilter
import models.CBCSubscription
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.Helper._

import javax.inject.Inject

class SubscriptionController @Inject() (cc: ControllerComponents, authFilter: AuthActionFilter) extends BackendController(cc) {

  def createSubscription(): Action[JsValue] = (Action(parse.json) andThen authFilter) { implicit request =>
    val regime = "CBC"

    request.body.asOpt[CBCSubscription] match {
      case Some(input) =>
        val organisationName = input.organisation
        val iDNumber         = input.iDNumber

        (input.regime, organisationName.organisationName, iDNumber) match {
          case (`regime`, "duplicate", _) => Conflict(resourceAsString("/resources/error/DuplicateSubmission.json").get)
          case (`regime`, "server", _)    => ServiceUnavailable(resourceAsString(s"/resources/error/ServiceUnavailable.json").get)
          case (`regime`, "notFound", _)  => NotFound(resourceAsString(s"/resources/error/RecordNotFound.json").get)
          case (`regime`, _, "XE0000123456789") =>
            Ok(resourceAsString("/resources/subscription/CBCCreateSubscriptionResponse.json").map(replaceSubscriptionId(_, "XAMDR0000111111")).get)
          case (`regime`, _, "XE0000987654321") =>
            Ok(resourceAsString("/resources/subscription/CBCCreateSubscriptionResponse.json").map(replaceSubscriptionId(_, "XAMDR0000222222")).get)
          case (`regime`, _, "XE5555523456789") =>
            Ok(resourceAsString("/resources/subscription/CBCCreateSubscriptionResponse.json").map(replaceSubscriptionId(_, "XAMDR0000444444")).get)
          case (`regime`, _, _) =>
            Ok(resourceAsString("/resources/subscription/CBCCreateSubscriptionResponse.json").map(replaceSubscriptionId(_, "XAMDR000033333")).get)
          case _ => BadRequest
        }
      case _ => BadRequest
    }
  }

  def readSubscription(): Action[JsValue] = Action(parse.json) { implicit request =>
    val json     = request.body
    val idNumber = (json \ "displaySubscriptionForCBCRequest" \ "requestDetail" \ "IDNumber").as[String]

    idNumber match {
      case "XACBC0000123777" =>
        Ok(
          resourceAsString(s"/resources/subscription/displayExistingUserSubscription.json")
            .map(r => replaceSubscriptionId(r, "XACBC0000123777"))
            .get
        )
      case "XACBC0000123778" =>
        Ok(
          resourceAsString(s"/resources/subscription/displaySubscription.json")
            .map(r => replaceSubscriptionId(r, "XACBC0000123778"))
            .get
        )
      case _ => ServiceUnavailable(resourceAsString(s"/resources/error/ServiceUnavailable.json").get)
    }
  }

  private def replaceSubscriptionId(response: String, subscriptionId: String): String =
    response.replace("[subscriptionId]", subscriptionId)
}
