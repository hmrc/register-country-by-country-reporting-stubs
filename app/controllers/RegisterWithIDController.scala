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
import models.{RegisterWithID, RequestWithIDDetails, WithIDOrganisation}
import play.api.libs.json.JsValue
import play.api.mvc.{Action, ControllerComponents, Result}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.Helper._

import javax.inject.Inject

class RegisterWithIDController @Inject() (cc: ControllerComponents, authFilter: AuthActionFilter) extends BackendController(cc) {

  def register: Action[JsValue] = (Action(parse.json) andThen authFilter) { implicit request =>
    val model         = request.body.as[RegisterWithID]
    val requestDetail = model.registerWithIDRequest.requestDetail
    val idNumber      = requestDetail.IDNumber

    idNumber match {
      case "9999999990" => InternalServerError(resourceAsString(s"/resources/error/InternalServerError.json").get)
      case "9999999991" => BadRequest(resourceAsString(s"/resources/error/BadRequest.json").get)
      case "9999999992" => ServiceUnavailable(resourceAsString(s"/resources/error/ServiceUnavailable.json").get)
      case "9999999993" => ServiceUnavailable(resourceAsString(s"/resources/error/RequestCouldNotBeProcessed.json").get)
      case "9999999994" => NotFound(resourceAsString(s"/resources/error/RecordNotFound.json").get)
      case utr =>
        (utr match {
          case "1234567890" => Some("XE0000123456789")
          case "1234567891" => Some("XE0000987654321")
          case "2222222222" => Some("XACBC0000123777")
          case "3333333333" => Some("XACBC0000123888")
          case _            => None
        }).flatMap { safeId =>
          val path = getWithIdResponse(requestDetail)
          resourceAsString(path).map(_.replace("[safeId]", safeId))
        }.fold[Result](NotFound)(Ok(_))
    }
  }

  private def getWithIdResponse(requestDetail: RequestWithIDDetails): String =
    requestDetail.partnerDetails match {
      case organisation: WithIDOrganisation
          if organisation.organisationName.toLowerCase() == "cbc company" | organisation.organisationName.toLowerCase() == "second company" =>
        s"/resources/register/utr/withIdResponse.json"
      case _ => "invalidPath"

    }
}
