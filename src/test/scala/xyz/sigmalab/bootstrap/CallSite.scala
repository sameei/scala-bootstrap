package xyz.sigmalab.bootstrap

import akka.testkit.TestKit
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.ActorMaterializer
import org.scalatest.{AsyncFlatSpec, AsyncFlatSpecLike, MustMatchers}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import xyz.sigmalab.bootstramp.HttpUtil

class CallSite
    extends TestKit(ActorSystem("testkit"))
            with  AsyncFlatSpecLike
            with MustMatchers {

    implicit val materializer = ActorMaterializer()

    it must "call google.com" in {

        Http().singleRequest(HttpRequest(uri = "https://akka.io"))
            .flatMap{ rsp => HttpUtil.collectUTF8(rsp.entity.dataBytes)}
            .map{ s => info(s) }
            .map { _ => succeed }
    }

}
