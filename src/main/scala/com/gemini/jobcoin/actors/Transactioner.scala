package com.gemini.jobcoin.actors

import akka.actor.{Actor, ActorLogging}
import com.gemini.jobcoin.actors.Transactioner._

object Transactioner{
  case class Transaction(to: String, amount: BigDecimal, fee: BigDecimal = BigDecimal(0))
  case class TransferMoney(transaction: Transaction)
  case class TransferToHouse(addr: String, amount: BigDecimal)
  case class TransLogEntry(addr: String, amt: BigDecimal)
  case class TransLog(logs: List[TransLogEntry])
  case class GetHouseTransLog()
  case class GetUserTransLog()
}

class Transactioner extends Actor with ActorLogging{

  @volatile var houseBalance = BigDecimal(0)
  @volatile var houseTransLog: List[TransLogEntry] = List.empty
  @volatile var userTransLog: List[TransLogEntry] = List.empty

  def transferMoney(trans: Transaction) = {
    System.out.println("making fake call to complete user transaction")
    userTransLog ++ List(TransLogEntry(trans.to,trans.amount - trans.fee))
  }

  def transferToHouse(addr: String, amt: BigDecimal) ={
    System.out.println("making fake call to complete house transaction")
    houseBalance += amt
    houseTransLog ++ List(TransLogEntry(addr,amt))
  }

  override def receive: Receive = {
    case TransferMoney(x) => transferMoney(x)
    case TransferToHouse(addr,amt) => transferToHouse(addr, amt)
    case GetHouseTransLog() => houseTransLog
    case GetUserTransLog() => userTransLog
  }
}
