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

import java.text.{DateFormat, SimpleDateFormat}

import org.scalatest.{FlatSpec, Matchers}
import spray.json._

class JavaDatesAsStringFormatTest extends FlatSpec with Matchers {

  case class HelloUtilDate(date: java.util.Date)
  case class HelloSqlDate(date: java.sql.Date)
  case class HelloSqlTimestamp(timestamp: java.sql.Timestamp)

  /**
    * RFC-822 only allows the 8 North American time zones to be named (4 standard time, 4 daylight savings time)
    * for all others, you have to specify the hour offset.
    *
    * see: http://www.w3.org/Protocols/rfc822/
    */
  object Rfc822 {
    val fullDateTimeWithMillis: String = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZ"
  }

  /**
    * ISO 8601 applies to representations and formats of dates in the Gregorian calendar, times based on the 24-hour
    * timekeeping system (including optional time zone information), time intervals and combinations thereof.
    *
    * see: http://en.wikipedia.org/wiki/ISO_8601
    */
  object Iso8601 {
    val fullDateTimeWithMillis: String = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

    val fullDateTimeWithSeconds: String = "yyyy-MM-dd'T'HH:mm:ssXXX"

    val fullDateTimeWithMinutes: String = "yyyy-MM-dd'T'HH:mmXXX"
  }

  object DateMarshallers extends DefaultJsonProtocol {
    implicit val jsonUtilDateFormat: JsonFormat[java.util.Date] = jsonDateFormatConstructor[java.util.Date](millis => new java.util.Date(millis), new SimpleDateFormat(Iso8601.fullDateTimeWithMillis))
    implicit val jsonSqlDate: JsonFormat[java.sql.Date] = jsonDateFormatConstructor[java.sql.Date](millis => new java.sql.Date(millis), new SimpleDateFormat(Iso8601.fullDateTimeWithMillis))
    implicit val jsonTimestamp: JsonFormat[java.sql.Timestamp] = jsonDateFormatConstructor[java.sql.Timestamp](millis => new java.sql.Timestamp(millis), new SimpleDateFormat(Iso8601.fullDateTimeWithMillis))

    def jsonDateFormatConstructor[T <: java.util.Date](f: (Long => T), formatter: DateFormat) = new JsonFormat[T] {
      def write(date: T) = JsString(formatter.format(date))

      def read(json: JsValue): T = json match {
        case JsString(s) => f(formatter.parse(s).getTime)
        case e => throw new spray.json.DeserializationException("unknown type: " + e)
      }
    }

    implicit val helloUtilDateFormat = jsonFormat1(HelloUtilDate)
    implicit val helloSqlDateFormat = jsonFormat1(HelloSqlDate)
    implicit val helloSqlTimestampFormat = jsonFormat1(HelloSqlTimestamp)
  }


  import DateMarshallers._

  "HelloUtilDate" should "be marshalled" in {
    HelloUtilDate(new java.util.Date(126226800000L)).toJson.compactPrint shouldBe """{"date":"1974-01-01T00:00:00.000+01:00"}"""
  }

  it should "be unmarshalled" in {
    """{"date":"1974-01-01T00:00:00.000+01:00"}""".parseJson.convertTo[HelloUtilDate] shouldBe HelloUtilDate(new java.util.Date(126226800000L))
  }

  "HelloSqlDate" should "be marshalled" in {
    HelloSqlDate(new java.sql.Date(126226800000L)).toJson.compactPrint shouldBe """{"date":"1974-01-01T00:00:00.000+01:00"}"""
  }

  it should "be unmarshalled" in {
    """{"date":"1974-01-01T00:00:00.000+01:00"}""".parseJson.convertTo[HelloSqlDate] shouldBe HelloSqlDate(new java.sql.Date(126226800000L))
  }

  "HelloSqlTimestamp" should "be marshalled" in {
    HelloSqlTimestamp(new java.sql.Timestamp(126226800000L)).toJson.compactPrint shouldBe """{"timestamp":"1974-01-01T00:00:00.000+01:00"}"""
  }

  it should "be unmarshalled" in {
    """{"timestamp":"1974-01-01T00:00:00.000+01:00"}""".parseJson.convertTo[HelloSqlTimestamp] shouldBe HelloSqlTimestamp(new java.sql.Timestamp(126226800000L))
  }
}
