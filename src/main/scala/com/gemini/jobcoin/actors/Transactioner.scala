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
  @volatile var houseTransLog: TransLog = TransLog(List.empty)
  @volatile var userTransLog: TransLog = TransLog(List.empty)

  def transferMoney(trans: Transaction): Unit = {
    System.out.println("making fake call to complete user transaction")
    houseBalance -= trans.amount
    userTransLog.logs ++ List(TransLogEntry(trans.to,trans.amount - trans.fee))
  }

  def transferToHouse(addr: String, amt: BigDecimal) : String ={
    System.out.println("making fake call to complete house transaction")
    houseBalance += amt
    houseTransLog.logs ++ List(TransLogEntry(addr,amt))
    "Successfully deposited your money!"
  }

  def getUserLog : TransLog = userTransLog

  def getHouseLog : TransLog = houseTransLog

  override def receive: Receive = {
    case TransferMoney(x) => transferMoney(x)
    case TransferToHouse(addr,amt) => sender ! transferToHouse(addr, amt)
    case GetHouseTransLog() => sender ! getHouseLog
    case GetUserTransLog() => sender ! getUserLog
  }
}
