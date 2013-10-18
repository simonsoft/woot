package spiralarm
package woot

import org.specs2.mutable._
import net.liftweb.json.{DefaultFormats, Extraction}
import net.liftweb.json.JsonAST._
import net.liftweb.json.JsonDSL._
import JsonFormats._

class JsonFormatsSpec extends Specification {

  implicit val formats = DefaultFormats

  "WString JSON format" should {

    "decode a JavaScript insert" in {

      // Example operation as received from a clent:
      val op = List(
       JField("op",JString("ins")),
       JField("from",JString("1597264050")),
       JField("wchar",JObject(List(
         JField("alpha",JString("A")),
         JField("id",JObject(List(JField("site",JString("1597264050")), JField("clock",JInt(19))))),
         JField("prev",JObject(List(JField("beginning",JBool(true))))),
         JField("next",JObject(List(JField("ending",JBool(true))))),
         JField("isVisible",JBool(true))))
       ))

      op.extractOpt[JOp] must beSome[JOp]

    }

    "encode queue" in {

      val cannotApply = InsertOp(WChar(CharId("1",1), 'A', Beginning, CharId("9999",9)), "1")

      val json : JValue =
        WString().integrate(cannotApply).queue.map(toJson).map(Extraction.decompose)

      val expected : JValue =
        ("op" ->"ins") ~  ("from" -> "1") ~ ("wchar" ->
          ("alpha" -> "A") ~
            ("id" -> (("site" -> "1") ~ ("clock" -> 1))) ~
            ("prev" -> ("beginning" -> true)) ~
            ("next" -> (("site" -> "9999") ~ ("clock" -> 9))) ~
            ("isVisible" -> true)
          ) :: Nil

      json must_== expected
    }


  }

}
