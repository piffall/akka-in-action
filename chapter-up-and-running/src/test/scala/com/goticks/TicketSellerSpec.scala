package com.goticks

import akka.actor.{Props, ActorSystem}

import akka.testkit.{ImplicitSender, TestKit}

import org.scalatest.{WordSpecLike, MustMatchers}

class TickerSellerSpec extends TestKit(ActorSystem("testTickets"))
                         with WordSpecLike
                         with MustMatchers
                         with ImplicitSender
                         with StopSystemAfterAll {
  "The TicketSeller" must {
    "Give out tickets until they are sold out" in {
      import TicketSeller._

      def mkTickets = (1 to 10).map(i=>Ticket(i)).toVector

      val ticketingActor = system.actorOf(TicketSeller.props("RHCP"))

      ticketingActor ! Add(mkTickets)
      ticketingActor ! Buy

      expectMsg(Some(Ticket(1)))

      val nrs = (2 to 10)
      nrs.foreach(_ => ticketingActor ! Buy)

      val tickets = receiveN(9)
      tickets.zip(nrs).foreach { case (Some(Ticket(tid)), id) => tid must be(id) }

      ticketingActor ! Buy
      expectMsg(None)
    }
  }
}