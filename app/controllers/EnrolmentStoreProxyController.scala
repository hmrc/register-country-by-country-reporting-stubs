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
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import utils.Helper._

import javax.inject.Inject

class EnrolmentStoreProxyController @Inject() (cc: ControllerComponents, authFilter: AuthActionFilter) extends BackendController(cc) {
  val mdrService      = "HMRC-CBC-ORG~CBCID~XACBC0000123777"
  val mdrServiceEmpty = "HMRC-CBC-ORG~CBCID~XACBC0000123888"

  def status(serviceName: String): Action[AnyContent] = (Action andThen authFilter) { _ =>
    serviceName match {
      case `mdrService` =>
        val path = "/resources/groupsES1/enrolment-response-with-groupid.json"
        Ok(resourceAsString(path).get)
      case `mdrServiceEmpty` =>
        val path = "/resources/groupsES1/enrolment-response-with-no-groupid.json"
        Ok(resourceAsString(path).get)
      case _ => NoContent
    }
  }
}
