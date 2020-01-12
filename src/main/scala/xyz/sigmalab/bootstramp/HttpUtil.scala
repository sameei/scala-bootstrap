package xyz.sigmalab.bootstramp

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, MessageEntity, ResponseEntity}
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.typesafe.config.{Config => Cfg}

import scala.concurrent.{ExecutionContext, Future}

object HttpUtil {

    val marshallerOfFutureMessageEntity = {
        import akka.http.scaladsl.marshalling.Marshaller
        import akka.http.scaladsl.marshalling.GenericMarshallers._
        futureMarshaller(liftMarshaller(Marshaller.MessageEntityMarshaller))
    }

    def marshallFutureHttpEntity(ft : Future[MessageEntity])  = {
        ToResponseMarshallable(ft)(marshallerOfFutureMessageEntity)
    }

    def collectUTF8(bs: Source[ByteString, _])(
        implicit
        executionContext: ExecutionContext,
        materializer: Materializer
    ) = {
        bs.runReduce{ (a,b) => a ++ b }
            .map{ bs => new String(bs.toArray, java.nio.charset.StandardCharsets.UTF_8) }
    }

    case class ServerConfig(host: String, port: Int)
    object ServerConfig {
        def from(values : Cfg) : ServerConfig = {
            ServerConfig(
                values.getString("host"),
                values.getInt("port")
            )
        }
    }

    def serve(
        config: ServerConfig,
        routes: Flow[HttpRequest, HttpResponse, _]
    )(
        implicit
        actorSystem : ActorSystem,
        streamMaterializer: Materializer,
        executionContext: ExecutionContext
    ) : Future[Http.ServerBinding] = {
        Http()(actorSystem)
            .bindAndHandle(routes, config.host, config.port)(streamMaterializer)
    }

}
