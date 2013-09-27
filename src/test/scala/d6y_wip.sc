object Worksheet {
import woots._
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

import woots.JsonFormats._
implicit val formats = DefaultFormats             //> formats  : net.liftweb.json.DefaultFormats.type = net.liftweb.json.DefaultFo
                                                  //| rmats$@3932f2b2

val xs = JObject(List(JField("op",JString("ins")), JField("from",JInt(1482024268)), JField("wchar",JObject(List(JField("alpha",JString("M")),
JField("id",JObject(List(JField("site",JInt(1482024268)), JField("clock",JInt(2))))),
JField("prev",JObject(List(JField("beginning",JBool(true))))), JField("next",JObject(List(JField("ending",JBool(true))))), JField("isVisible",JBool(true)))))))
                                                  //> xs  : net.liftweb.json.JsonAST.JObject = JObject(List(JField(op,JString(ins)
                                                  //| ), JField(from,JInt(1482024268)), JField(wchar,JObject(List(JField(alpha,JSt
                                                  //| ring(M)), JField(id,JObject(List(JField(site,JInt(1482024268)), JField(clock
                                                  //| ,JInt(2))))), JField(prev,JObject(List(JField(beginning,JBool(true))))), JFi
                                                  //| eld(next,JObject(List(JField(ending,JBool(true))))), JField(isVisible,JBool(
                                                  //| true)))))))


xs.extractOpt[JOp]                                //> res0: Option[woots.JsonFormats.JOp] = Some(JOp(ins,1482024268,JWChar(M,JId(N
                                                  //| one,None,None,None),JId(None,None,Some(true),None),JId(None,None,None,Some(t
                                                  //| rue)),true)))

val w = WChar(CharId(1,2), 'a', Beginning, Ending)//> w  : woots.WChar = WChar(CharId(1,2),a,woots.Beginning$@6f526c5f,woots.Endin
                                                  //| g$@c490a12,true)

val o = new InsertOp(w)                           //> o  : woots.InsertOp = InsertOp(WChar(CharId(1,2),a,woots.Beginning$@6f526c5f
                                                  //| ,woots.Ending$@c490a12,true))


val i = ("beginning"->true)                       //> i  : (String, Boolean) = (beginning,true)
i.extractOpt[JId]                                 //> res1: Option[woots.JsonFormats.JId] = Some(JId(None,None,Some(true),None))

val i2 = ("site"-> -457072000) ~ ("clock"->2)     //> i2  : net.liftweb.json.JsonAST.JObject = JObject(List(JField(site,JInt(-4570
                                                  //| 72000)), JField(clock,JInt(2))))
i2.extractOpt[JId]                                //> res2: Option[woots.JsonFormats.JId] = Some(JId(None,None,None,None))

//val json =  ("alpha"->"M") ~ ("isVisible"->true) ~



val j = toJson(w)                                 //> j  : woots.JsonFormats.JWChar = JWChar(a,JId(Some(1),Some(2),None,None),JId(
                                                  //| None,None,Some(true),None),JId(None,None,None,Some(true)),true)
Extraction.decompose(j)                           //> res3: net.liftweb.json.JValue = JObject(List(JField(alpha,JString(a)), JFiel
                                                  //| d(id,JObject(List(JField(site,JInt(1)), JField(clock,JInt(2)), JField(beginn
                                                  //| ing,JNothing), JField(ending,JNothing)))), JField(prev,JObject(List(JField(s
                                                  //| ite,JNothing), JField(clock,JNothing), JField(beginning,JBool(true)), JField
                                                  //| (ending,JNothing)))), JField(next,JObject(List(JField(site,JNothing), JField
                                                  //| (clock,JNothing), JField(beginning,JNothing), JField(ending,JBool(true))))),
                                                  //|  JField(isVisible,JBool(true))))
Printer.pretty(render(Extraction.decompose(j)))   //> res4: String = {
                                                  //|   "alpha":"a",
                                                  //|   "id":{
                                                  //|     "site":1,
                                                  //|     "clock":2
                                                  //|   },
                                                  //|   "prev":{
                                                  //|     "beginning":true
                                                  //|   },
                                                  //|   "next":{
                                                  //|     "ending":true
                                                  //|   },
                                                  //|   "isVisible":true
                                                  //| }

              
              
                         
}