package com.maritzcx.kafka.mgmt.api

import org.scalatra.test.scalatest.ScalatraSuite

/**
  * Created by bjacobs on 2/27/17.
  */
trait DynamicScalatraSuite extends ScalatraSuite {

//  override def baseUrl =

  override protected def beforeAll(): Unit = {

    // TODO: make this configurable

    start()
  }

  override protected def afterAll(): Unit = {

    // TODO: make this configurable

    stop()
  }

}
