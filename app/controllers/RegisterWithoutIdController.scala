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
import models.Register
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.Helper.resourceAsString

import javax.inject.Inject

class RegisterWithoutIdController @Inject() (cc: ControllerComponents, authFilter: AuthActionFilter) extends BackendController(cc) {

  def register: Action[JsValue] = (Action(parse.json) andThen authFilter) { implicit request =>
    val regimeName = "CBC"
    val register   = request.body.as[Register]
    val orgName    = register.organisation.organisationName

    (register.regime, orgName) match {
      case (`regimeName`, "error")        => InternalServerError(resourceAsString(s"/resources/error/InternalServerError.json").get)
      case (`regimeName`, "invalid")      => BadRequest(resourceAsString(s"/resources/error/BadRequest.json").get)
      case (`regimeName`, "server")       => ServiceUnavailable(resourceAsString(s"/resources/error/ServiceUnavailable.json").get)
      case (`regimeName`, "notProcessed") => ServiceUnavailable(resourceAsString(s"/resources/error/RequestCouldNotBeProcessed.json").get)
      case (`regimeName`, "notFound")     => NotFound(resourceAsString(s"/resources/error/RecordNotFound.json").get)
      case (`regimeName`, data) =>
        val safeId = data match {
          case "duplicate"    => "XE999923456789"
          case "enrolment"    => "XE3333333333333"
          case "organisation" => "XE5555523456789"
          case _              => "XE2222123456789"
        }
        resourceAsString(s"/resources/register/withoutIdResponse.json") match {
          case Some(response) => Ok(response.replace("[safeId]", safeId))
          case _              => NotFound
        }
      case _ => BadRequest
    }
  }

}
