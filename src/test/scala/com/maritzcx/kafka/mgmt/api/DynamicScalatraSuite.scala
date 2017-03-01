package com.maritzcx.kafka.mgmt.api

import com.maritzcx.kafka.mgmt.api.config.ConfigManager
import org.scalatra.test.scalatest.ScalatraSuite

/**
  * Created by bjacobs on 2/27/17.
  */
trait DynamicScalatraSuite extends ScalatraSuite {


  val shouldEnableJetty = ConfigManager.config.getBoolean("should.enable.jetty")

  override def baseUrl = shouldEnableJetty match {

    // let scalatra spin up a local jetty server for the tests and use localhost:<availableport>
    case true => super.baseUrl

    // assign a baseUrl for the rest calls for scalatra
    case false => ConfigManager.config.getString("kmra.url")
  }

  override protected def beforeAll(): Unit = {

    if(shouldEnableJetty)
      start()

  }

  override protected def afterAll(): Unit = {

    if(shouldEnableJetty)
      stop()
  }

}
