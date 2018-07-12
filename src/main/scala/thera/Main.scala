package thera

import scala.collection.JavaConverters._

import java.io.File
import org.apache.commons.io.FileUtils

import cats._, cats.implicits._

import io.circe._


object Main {
  def main(args: Array[String]): Unit = {
    val input  = new File("site-src/posts/2017-03-10-matryoshka-intro.md")
    val output = new File("_site/result.html")
    val data   = new File("site-src/data/data.yml")
    val assets = new File("site-src/assets")

    run { for {
      // Config
      configRaw <- att { FileUtils.readFileToString(data, settings.enc) }
      config    <- exn { yaml.parser.parse(configRaw) }
      vars      <- exn { config.as[Map[String, String]] }
      _         <- att { println(s"Vars parsed:\n${vars.mkString("\n")}") }

      // Assemble assets
      _   <- att { FileUtils.copyDirectory(assets, new File("_site", "assets")) }
      css <- templates(
        new File("site-src/private-assets/css/all.css")
      , fragmentResolver = name => new File(s"site-src/private-assets/css/${name}.css"))
      _   <- att { FileUtils.writeStringToFile(new File("_site/assets/all.css"), css, settings.enc) }

      // Process input post
      res <- templates(input, vars)
      _   <- att { FileUtils.writeStringToFile(output, res, settings.enc) }

      // _ <- index(vars)
    } yield () }
  }

  // def index(vars: Map[String, String]): Ef[Unit] =
  //   for {

  //   }
}