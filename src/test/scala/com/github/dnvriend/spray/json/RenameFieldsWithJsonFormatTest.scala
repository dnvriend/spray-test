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

import org.scalatest.{ FlatSpec, Matchers }
import spray.json.{ DefaultJsonProtocol, _ }

class RenameFieldsWithJsonFormatTest extends FlatSpec with Matchers {

  case class HelloWorld(first: String, last: String)

  object Marshallers extends DefaultJsonProtocol {
    implicit val helloWorldFormat = jsonFormat(HelloWorld, "hello", "world")
  }

  import Marshallers._

  it should "rename fields with a jsonFormat" in {
    HelloWorld("foo", "bar").toJson.compactPrint shouldBe """{"hello":"foo","world":"bar"}"""
  }

  it should "unmarshal json to HelloWorld, notice the field names" in {
    val helloWorld = """{"hello":"foo","world":"bar"}""".parseJson.convertTo[HelloWorld]
    helloWorld.first shouldBe "foo"
    helloWorld.last shouldBe "bar"
  }
}
