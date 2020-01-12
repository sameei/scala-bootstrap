package xyz.sigmalab.bootstramp

import com.typesafe.config.{Config, ConfigFactory}
import org.slf4j.LoggerFactory
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Try, Success, Failure}

object Launcher {

    def main(args: Array[String]) : Unit = {

        val conf = ConfigFactory.load().resolve();

        val logger = LoggerFactory.getLogger("Launcher")

        logger.info(s"AppKey : ${conf.getString("app.key")}")

        logger.info(s"Init ActorSystem & Materializer ...")

        implicit val actorSystem = ActorSystem("application", conf)

        implicit val materializer = ActorMaterializer()

        val httpConf = HttpUtil.ServerConfig.from(conf.getConfig("http"))

        // println(new GoogleCall().call())

        logger.info(s"Init HttpServer with ${httpConf} ...")
        val binding = Await.result(
            HttpUtil.serve(httpConf, new HttpApplication().route02),
            1 minute
        )

        logger.info("Waiting for [ENTER/RETURN] ...")
        scala.io.StdIn.readLine()

        logger.info("Shutting down ...")

        Try { Await.result(
            actorSystem.terminate().flatMap { _ =>
                binding.unbind()
            },
            1 minute
        )}.recover {
            case cause : Throwable => sys.runtime.halt(1)
        }
    }

}
