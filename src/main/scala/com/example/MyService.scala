package com.example

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// Spray-routing makes all relevant parts of the routing DSL available through the HttpService trait, which you can mix into your service actor
trait MyService extends HttpService {

  // The myRoute is of type 'spray.routing.Route' and is defined using spray-routing DSL for expressing your service behavior as a structure of
  // composable elements (called Directives) in a concise and readable way. The 'complete' directive creates the spray.routing.Route which is returned
  // The type of Route is a function of type 'RequestContext => Unit' so it's a simple alias for a function taking a RequestContext as parameter.
  //
  // Please read the following documentation:
  //
  // read: http://spray.io/documentation/1.2.1/spray-routing/key-concepts/big-picture/
  // read: http://spray.io/documentation/1.2.1/spray-routing/key-concepts/routes/#routes
  //
  val myRoute: Route =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }

  // ==============================================================================================================
  // There are a lot of 'directives' predefined in spray-routing. Directives do one or more of the following things:
  //
  // 1. Filtering of the request or Extracting values from the request
  // 2. Creating a response or transforming the response,
  //
  // Directives are therefore organized into traits that conform to the behavior mentioned above:
  // (see: http://spray.io/documentation/1.2.1/spray-routing/predefined-directives-by-trait/),
  //
  // but basically it boils down to this (there are more, please see the web site!!!)
  //
  // ============================
  // For Filtering or Extracting:
  // ============================
  // * MethodDirectives: Filter and extract based on the request method: delete, get, head, method, options, patch, post, put
  // * PathDirectives: Filter and extract from the request URI path: path, pathPrefix, pathSuffix,
  // * SecurityDirectives: Handle authentication data from the request: authenticate, authorize
  //
  // ==========================================
  // For creating or transforming the response:
  // ==========================================
  // * RouteDirectives: Complete or reject a request with a response: complete, failWith, redirect, reject
  // * RespondWithDirectives: Change response properties: respondWithHeader, respondWithMediaType, respondWithStatus
  // ==============================================================================================================


}