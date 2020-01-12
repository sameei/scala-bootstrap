package xyz.sigmalab.bootstramp

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer
import akka.stream.scaladsl.Framing
import akka.util.ByteString
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

final case class Foo(bar: String)

class HttpApplication {

    import de.heikoseeberger.akkahttpjackson.JacksonSupport._
    import akka.http.scaladsl.server.Directives._
    import scala.concurrent.duration._
    import akka.stream.scaladsl.Source
    import akka.http.scaladsl.Http
    import akka.http.scaladsl.unmarshalling.Unmarshal
    import akka.http.scaladsl.marshalling.Marshaller

    // complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))

    def route01(implicit actorSystem: ActorSystem) = {

        path("hello") {
            get {
                complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello :)"))
            }
        } ~ pathPrefix("api") {

            path("public") {
                get {
                    complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hey :)"))
                }
            } ~ path("private") {
                get {
                    complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "FuckOff"))
                }
            }
        } ~ path("api") {
            get {
                complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "API :)"))
            }
        } ~ path("foo") {
            post {
                entity(as[Foo]) { foo =>
                    complete {
                        foo
                    }
                }
            }
        }
    }

    val logger = LoggerFactory.getLogger("HTTP.API")

    def route02(
        implicit
        actorSystem: ActorSystem,
        materializer : Materializer,
        executionContext: ExecutionContext
    ) = {

        pathSingleSlash {
            post {
                entity(as[Foo]) { foo =>
                    complete {
                        foo
                    }
                }
            } ~ get {
                logger.info("GET /")
                val response =
                    Http()
                        .singleRequest(HttpRequest(uri = "http://google.com"))
                        .map {_.entity.dataBytes}
                        .map{ bs => HttpEntity.Chunked.fromData(ContentTypes.`text/html(UTF-8)`, bs) }
                complete(HttpUtil.marshallFutureHttpEntity(response))
            } ~ path("hello") {
                logger.info("GET /hello")
                get { complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello")) }
            }
        } ~ pathPrefix("stream") {
            post {
                logger.info("POST /stream")
                entity(as[SourceOf[Foo]]) { fooSource: SourceOf[Foo] =>
                    complete(fooSource.throttle(1, 2.seconds))
                }
            } ~ get {
                pathEndOrSingleSlash {
                    complete(
                        Source(0 to 5)
                            .throttle(1, 1.seconds)
                            .map(i => Foo(s"bar-$i"))
                    )
                } ~ pathPrefix("remote") {
                    onSuccess(Http().singleRequest(HttpRequest(uri = "http://localhost:8000/stream"))) {
                        response =>
                            complete(Unmarshal(response).to[SourceOf[Foo]])
                    }
                }
            }
        } ~ path("file") {
            extractRequestContext { ctx =>
                // https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/file-upload-directives/fileUpload.html
                fileUpload("fileContent") {
                    case (meta, bs) =>
                        val sumF: Future[Int] =
                        // sum the numbers as they arrive so that we can
                        // accept any size of file
                        bs.via(Framing.delimiter(ByteString("\n"), 1024 * 1024))
                                .mapConcat(_.utf8String.split(",").toVector)
                                .map(_.toInt)
                                .runFold(0) { (acc, n) => acc + n }
                        onSuccess(sumF) {
                            sum => complete(s"Sum: $sum")
                        }
                }
            }
        } ~ pathPrefix("routing-tricks") {
            ???
        }

    }
}
