import com.maritzcx.kafka.mgmt.api.config.{RouteHandler, AppModule}
import com.maritzcx.kafka.mgmt.api.rest.{TopicRest}

import org.scalatra._
import javax.servlet.ServletContext

import scaldi.Injectable

class ScalatraBootstrap extends LifeCycle with Injectable {

  implicit val injector = new AppModule().injector

  override def init(context: ServletContext) {

    context.mount(inject[TopicRest], RouteHandler.TOPIC)

  }
}
