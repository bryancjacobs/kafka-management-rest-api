package com.maritzcx.kafka.mgmt.api.rest

import com.maritzcx.kafka.mgmt.api.RestSupport
import org.json4s.jackson.JsonMethods.parse

class KafkaScalatraRestIT extends RestSupport{

  addServlet(inject[KafkaScalatraRest], "/*")

  "getAll" should "return all objects" in {

    get(uri = "/", headers = getAuthHeader()){

      status should equal (200)

      val json = parse(body)

      val words = json.extract[List[String]]

      words.size shouldBe 2

      words shouldBe List("hello", "friend")

    }

  }

}
