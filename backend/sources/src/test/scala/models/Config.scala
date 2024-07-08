package models

import play.api.libs.json._
import scala.io.Source

case class Config(
        mtime: BigInt,
        mtimeh: BigInt,
        mtimecmp: BigInt,
        mtimecmph: BigInt,
        keyboard_ready: BigInt,
        keyboard_data: BigInt,
        display_ready: BigInt,
        display_data: BigInt,
        initial_pc: BigInt,
        machine_program_file: String,
        user_program_files: Map[BigInt, (String, String)],
        initial_memory: Map[BigInt, BigInt],
        display_delay: BigInt
    ) {
    }
    object Config {
        implicit val reads : Reads[Config] = new Reads[Config] {
            def reads(json: JsValue): JsResult[Config] = {
                JsSuccess(new Config(
                    BigInt((json \ "memory_mapped_registers" \ "mtime").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "mtimeh").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "mtimecmp").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "mtimecmph").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "keyboard_ready").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "keyboard_data").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "display_ready").as[String].substring(2), 16),
                    BigInt((json \ "memory_mapped_registers" \ "display_data").as[String].substring(2), 16),
                    BigInt((json \ "initial_pc").as[String].substring(2), 16),
                    (json \ "os_program_file").as[String],
                    (json \ "user_programs").as[Seq[Map[String, String]]].map(x => BigInt(x("address").substring(2), 16) -> (x("name"), x("file"))).toMap,
                    (json \ "initial_memory").as[Map[String, String]].map(x => BigInt(x._1.substring(2), 16) -> BigInt(x._2.substring(2), 16)),
                    BigInt((json \ "display_delay").as[String])
                ))
            }
        }

        def loadConfig(path: String): Config = {
            val string = Source.fromResource(path).mkString
            val json = Json.parse(string)
            return json.as[Config]
        }
    }