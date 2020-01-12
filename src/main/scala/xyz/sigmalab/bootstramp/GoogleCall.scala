package xyz.sigmalab.bootstramp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest}
import akka.stream.Materializer
import scala.concurrent.duration._

import scala.concurrent.{Await, ExecutionContext}

class GoogleCall()(
    implicit
    val actorSystem : ActorSystem,
    val streamMaterializer : Materializer,
    val executionContext : ExecutionContext
) {

    def call() = {
        val ft = Http()
            .singleRequest(HttpRequest(uri = "http://akka.io"))
            .flatMap {_.entity.dataBytes.runReduce { (a,b) => a ++ b } }
            .map{ bs => HttpEntity.Strict(ContentTypes.`text/html(UTF-8)`, bs) }
        Await.result(ft, 1 minutes)
    }

}
