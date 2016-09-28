package com.maritzcx.kafka.mgmt.api

import org.scalatra._

class KafkaScalatraServlet extends KafkaManagementRestApiStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }

}
