/*
 * Copyright 2016 Dennis Vriend
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

package com.example

import org.scalatest.{ FlatSpec, Matchers }
import spray.json.{ DefaultJsonProtocol, RootJsonFormat, _ }

trait PersonEvent {
  def personId: Long
}
case class PersonCreated(val personId: Long, firstName: String, lastName: String) extends PersonEvent

case class FirstNameChanged(val personId: Long, oldName: String, newName: String) extends PersonEvent

case class LastNameChanged(val personId: Long, oldName: String, newName: String) extends PersonEvent

class PolymorphicMarshalTest extends FlatSpec with Matchers {

  object PersonEventMarshallers extends DefaultJsonProtocol {
    implicit val personCreatedFormat = jsonFormat3(PersonCreated)
    implicit val firstNameChangedFormat = jsonFormat3(FirstNameChanged)
    implicit val lastNameChangedFormat = jsonFormat3(LastNameChanged)

    implicit object PersonEventJsonFormat extends RootJsonFormat[PersonEvent] {
      import spray.json._

      final val PersonCreatedFQCN = PersonCreated.getClass.getName.dropRight(1) // to get rid of the `$` of the companion object
      final val FirstNameChangedFQCN = FirstNameChanged.getClass.getName.dropRight(1) // to get rid of the `$` of the companion object
      final val LastNameChangedFQCN = LastNameChanged.getClass.getName.dropRight(1) // to get rid of the `$` of the companion object

      def jsonTypeInfo(e: PersonEvent): Map[String, JsValue] =
        Map("@class" -> JsString(e.getClass.getName))

      def convertTo[T <: PersonEvent](map: Map[String, JsValue])(implicit reader: JsonReader[T]): T =
        (map - "@class").toJson.convertTo[T]

      override def write(event: PersonEvent): JsValue = event match {
        case e: PersonCreated    ⇒ JsObject(jsonTypeInfo(e) ++ e.toJson.asJsObject.fields)
        case e: FirstNameChanged ⇒ JsObject(jsonTypeInfo(e) ++ e.toJson.asJsObject.fields)
        case e: LastNameChanged  ⇒ JsObject(jsonTypeInfo(e) ++ e.toJson.asJsObject.fields)
      }

      override def read(value: JsValue): PersonEvent = {
        val map = value.asJsObject.fields
        val xx = map("@class")
        xx match {
          case JsString(`PersonCreatedFQCN`) ⇒ convertTo[PersonCreated](map)
          case JsString(`FirstNameChangedFQCN`) ⇒ convertTo[FirstNameChanged](map)
          case JsString(`LastNameChangedFQCN`) ⇒ convertTo[LastNameChanged](map)
          case e => throw new spray.json.DeserializationException("unknown json type information: " + e)
        }
      }
    }
  }

  import spray.json._
  import PersonEventMarshallers._

  "PersonCreated" should "be marshalled" in {
    PersonCreated(1, "fn", "ln").asInstanceOf[PersonEvent].toJson.compactPrint shouldBe """{"@class":"com.example.PersonCreated","personId":1,"firstName":"fn","lastName":"ln"}"""
  }

  it should "be unmarshalled" in {
    """{"@class":"com.example.PersonCreated","personId":1,"firstName":"fn","lastName":"ln"}""".parseJson.convertTo[PersonEvent] should matchPattern {
      case PersonCreated(1, "fn", "ln") =>
    }
  }


  "FirstNameChanged" should "be marshalled" in {
    FirstNameChanged(1, "old", "new").asInstanceOf[PersonEvent].toJson.compactPrint shouldBe """{"@class":"com.example.FirstNameChanged","personId":1,"oldName":"old","newName":"new"}"""
  }

  it should "be unmarshalled" in {
    """{"@class":"com.example.FirstNameChanged","personId":1,"oldName":"old","newName":"new"}""".parseJson.convertTo[PersonEvent] should matchPattern {
      case FirstNameChanged(1, "old", "new") =>
    }
  }

  "LastNameChanged" should "be marshalled" in {
    LastNameChanged(1, "old", "new").asInstanceOf[PersonEvent].toJson.compactPrint shouldBe """{"@class":"com.example.LastNameChanged","personId":1,"oldName":"old","newName":"new"}"""
  }

  it should "be unmarshalled" in {
    """{"@class":"com.example.LastNameChanged","personId":1,"oldName":"old","newName":"new"}""".parseJson.convertTo[PersonEvent] should matchPattern {
      case LastNameChanged(1, "old", "new") =>
    }
  }
}
