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

package com.github.dnvriend.spray.json

import com.github.dnvriend.TestSpec
import spray.json.{ DefaultJsonProtocol, NullOptions }
import spray.json._

trait PersonFormat extends DefaultJsonProtocol with NullOptions {
  final case class Person(firstName: String, surName: Option[String])
  implicit val personJsonFormat = jsonFormat2(Person)
}

class NullOptionTest extends TestSpec with PersonFormat {
  it should "marshal JSON with nulls where fields are optional" in {
    Person("foo", None).toJson.compactPrint shouldBe """{"firstName":"foo","surName":null}"""
  }
}
