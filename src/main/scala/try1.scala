import AppConstants.{AddToInventory, InventoryState, ProductCostState, UpdateCost}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object try1 extends App with LazyLogging {
//  implicit val timeout: Timeout = Timeout(5.seconds)
//  val system: ActorSystem = ActorSystem("shopping-system")
//  implicit val executionContext: ExecutionContext = system.dispatcher
//  val inventoryActor: ActorRef = system.actorOf(Props[Inventory], "shopping-actor")
//  val costActor: ActorRef = system.actorOf(Props(ProductCost(inventoryActor)))
//
//  inventoryActor ! AddToInventory("Keyboard", 10)
//  inventoryActor ! AddToInventory("Mouse", 20)
//
//  costActor ! UpdateCost("Keyboard", 5)
//
//  Thread.sleep(1000)
//  costActor ! ProductCostState
  val x = Map("wiubvrw" -> List("wiub", "scw"), "wicubec" -> List("iwurbv", "iqcube", "wicue"))
  val y = x.valuesIterator
  while(y.hasNext) {
    println(y.next())
  }
}
