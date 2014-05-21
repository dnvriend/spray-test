package com.example

import spray.http._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import MediaTypes._
import scala.xml.NodeSeq
import spray.json.DefaultJsonProtocol

/**
 * This case class will be used to marshal from and to a vendor specific (vnd) <a href="http://en.wikipedia.org/wiki/Internet_media_type">Internet Media Type</a>
 * and all other media types known below
 */
case class Person(name: String, firstName: String, age: Int)

/**
 * This is the companion object for the case class, it contains the vendor specific media type and some marshallers en unmarshallers
 * that must be made available where the (un)marshalling will be done by means of an import, see  <code>PersonMarshalTest</code>. That way
 * the specific marshaller will be made available implicitly so the compiler can use the value where an marshaller is needed
 */
object PersonMarshaller {
  val `application/vnd.acme.person` = MediaTypes.register(MediaType.custom("application/vnd.acme.person"))

  object PersonJsonProtocol extends DefaultJsonProtocol {
    implicit val personJsonFormat = jsonFormat3(Person)
  }

  /**
   * This value represents a Marshaller to the vendor specific format
   */
  implicit val VndAcmeMarshaller = Marshaller.of[Person](`application/vnd.acme.person`) { (value, contentType, ctx) =>
    val Person(name, first, age) = value
    val string = "Person: %s, %s, %s".format(name, first, age)
    ctx.marshalTo(HttpEntity(contentType, string))
  }

  /**
   * This value represents an Unmarshaller from a vendor specific format
   */
  implicit val VndAcmeUnmarshaller = Unmarshaller.delegate[String, Person](`application/vnd.acme.person`) { string =>
    val Array(_, name, first, age) = string.split(":,".toCharArray).map(_.trim)
    Person(name, first, age.toInt)
  }

  /**
   * This value represents a Marshaller to XML
   */
  implicit val XmlMarshaller = Marshaller.of[Person](`application/xml`) { (value, contentType, ctx) =>
    val Person(name, first, age) = value
    val xml =
      <person>
        <name>{ name }</name>
        <first>{ first }</first>
        <age>{ age }</age>
      </person>.toString().trim
    ctx.marshalTo(HttpEntity(contentType, xml))
  }

  /**
   * this value represents an Unmarshaller from XML
   */
  implicit val XmlUnmarshaller = Unmarshaller.delegate[NodeSeq, Person](`text/xml`, `application/xml`) { xml =>
    // The XmlString will be converted to a NodeSeq, implicitly by the default Spray marshallers, then
    // this marshaller will be used by the compiler to create the person
    Person((xml \ "name").text , (xml \ "first").text , (xml \ "age").text.toInt)
  }

}

