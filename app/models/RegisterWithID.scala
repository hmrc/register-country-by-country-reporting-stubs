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

package models

import play.api.libs.json._

case class RegisterWithID(registerWithIDRequest: RegisterWithIDRequest)

object RegisterWithID {
  implicit val reads: Reads[RegisterWithID] = Json.reads[RegisterWithID]
}

case class RegisterWithIDRequest(requestDetail: RequestWithIDDetails)

object RegisterWithIDRequest {
  implicit val reads: Reads[RegisterWithIDRequest] = Json.reads[RegisterWithIDRequest]
}

case class WithIDOrganisation(
  organisationName: String
)

object WithIDOrganisation {
  implicit val reads: Reads[WithIDOrganisation] = Json.reads[WithIDOrganisation]
}

case class RequestWithIDDetails(
  IDType: String,
  IDNumber: String,
  partnerDetails: WithIDOrganisation
)

object RequestWithIDDetails {

  implicit lazy val requestWithIDDetailsReads: Reads[RequestWithIDDetails] = {
    import play.api.libs.functional.syntax._
    (
      (__ \ "IDType").read[String] and
        (__ \ "IDNumber").read[String] and
        (__ \ "organisation").read[WithIDOrganisation]
    )((idType, idNumber, organisation) => RequestWithIDDetails(idType, idNumber, organisation))
  }
}
