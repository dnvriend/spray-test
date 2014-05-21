package com.example

import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import org.scalatest._
import org.specs2.matcher.Matchers
import spray.http._
import MediaTypes._
import com.example.Person.`application/vnd.acme.person`

class PersonMarshalTest extends FlatSpec with Matchers {
  "Marshal Person with vnd.acme.person" should "result in an HttpEntity with media type vnd.acme.person" in {
    // we pull the VndAcmeMarshaller, which is an implicit value, into scope. The marshal method has
    // a second parameter list that accepts an implicit value <code>(implicit marshaller: Marshaller[T]</code>
    // the compiler will find that marshaller for the conversion of 'application/vnd.acme.person' to Person
    import com.example.Person.VndAcmeMarshaller
    marshal(Person("Bob", "Parr", 32)) == Right(HttpEntity(`application/vnd.acme.person`, "Person: Bob, Parr, 32"))

    // we alternatively could have typed:
    marshal(Person("Bob", "Parr", 32))(marshaller = com.example.Person.VndAcmeMarshaller) == Right(HttpEntity(`application/vnd.acme.person`, "Person: Bob, Parr, 32"))
  }

  "Unmarshal Person with vnd.acme.person" should "result in a Person case class" in {
    // here the same deal, we pull the VndAcmeUnmarshaller in scope (for the compiler to use) because the '.as()' method
    // needs an implicit marshaller
    import com.example.Person.VndAcmeUnmarshaller
    val body = HttpEntity(`application/vnd.acme.person`, "Person: Bob, Parr, 32")
    body.as[Person] === Right(Person("Bob", "Parr", 32))

    // we alternatively could have typed:
    body.as[Person](com.example.Person.VndAcmeUnmarshaller) === Right(Person("Bob", "Parr", 32))
  }

  "Marshal Person with application/xml" should "result in an HttpEntity with media type application/xml" in {
    import com.example.Person.XmlMarshaller
    val xml =
      <person>
        <name>Bob</name>
        <first>Parr</first>
        <age>32</age>
      </person>.toString().trim
    val result = marshal(Person("Bob", "Parr", 32))
    result match {
      case Right(HttpEntity.NonEmpty(contentType, data)) =>
        assert(contentType.toString() === `application/xml`.toString())
        assert(data.asString === xml)
      case _ => fail
    }
  }

  "UnMarshal Person with application/xml" should "result in a Person case class" in {
    import com.example.Person.XmlUnmarshaller
    val xmlString =
      <person>
        <name>Bob</name>
        <first>Parr</first>
        <age>32</age>
      </person>.toString

    // the HttpEntity only accepts the value: string, Array[Byte], ByteString, HttpData
    val body = HttpEntity(`application/xml`, xmlString)
    body.as[Person] match {
      case Right(Person(name, first, age)) =>
        assert(name === "Bob")
        assert(first === "Parr")
        assert(age === 32)
      case _ => fail("")
    }
  }

  "UnMarshal Person with text/xml" should "result in a Person case class" in {
    import com.example.Person.XmlUnmarshaller
    val xmlString =
      <person>
        <name>Bob</name>
        <first>Parr</first>
        <age>32</age>
      </person>.toString

    // the HttpEntity only accepts the value: string, Array[Byte], ByteString, HttpData
    val body = HttpEntity(`text/xml`, xmlString)
    body.as[Person] match {
      case Right(Person(name, first, age)) =>
        assert(name === "Bob")
        assert(first === "Parr")
        assert(age === 32)
      case _ => fail("")
    }
  }
}
