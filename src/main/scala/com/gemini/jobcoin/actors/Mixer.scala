package com.gemini.jobcoin.actors

import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import com.gemini.jobcoin.actors.Mixer._
import com.gemini.jobcoin.actors.Transactioner.{Transaction, TransferMoney, TransferToHouse}

import scala.util.Random

object Mixer {
  case class InputAddresses(addresses: List[String])
  case class GenerateTransaction(address: String)
  case class GenerateAddress(inputAddresses: InputAddresses)
  case class UserState(transferTos: InputAddresses,
                       totalAmount: BigDecimal,
                       amountLeft: BigDecimal,
                       givenAddr: String,
                       pctfee: Float,
                       feeCharged: BigDecimal)
  case class ScheduleTrans()
  case class IncomingMoney(trans: Transaction)
  case class GeneratedAddress(address: String)
}
class Mixer extends Actor with ActorLogging{
  private val homeAddress = "mixer-1983-8260-2786-2232"
  @volatile private var stateMap: Map[String, UserState] = Map.empty
  def generateNewUserData(x: InputAddresses) : GeneratedAddress = {
    this.synchronized {
      val addrTo = UUID.randomUUID().toString
      val newUser = UserState(x, BigDecimal(0), BigDecimal(0), addrTo, 0.1F, BigDecimal(0))
      stateMap += (addrTo -> newUser)
      GeneratedAddress(addrTo)
    }
  }

  def scheduleTrans() = {
    stateMap map {x => if(x._2.amountLeft > 0) self ! GenerateTransaction(x._1) }
  }

  def generateTransaction(addr: String) = {
    val userState = stateMap.get(addr)
    userState match {
      case None => throw new IllegalArgumentException("Could not find user for which we are trying to generate transaction")
      case Some(x) => {
        val amtPct = Random.nextFloat()
        val amtToTransfer = x.amountLeft * amtPct.toDouble
        // Charge money if they hold a balance of more than million dollars
        val fee = x.amountLeft > BigDecimal(1000000) match {
          case true => amtToTransfer * x.pctfee.toDouble
          case false => BigDecimal(0)
        }
        val randIndex = Random.nextInt(x.transferTos.addresses.length)
        sender ! TransferMoney(Transaction(x.transferTos.addresses(randIndex),amtToTransfer,fee))
      }
    }
  }

  def updateUserBalance(trans: Transaction) = {
    this.synchronized {
      stateMap.get(trans.to) match {
        case None => throw new IllegalArgumentException("Could not find user for which money was send")
        case Some(x) => stateMap += (trans.to -> x.copy(totalAmount = x.totalAmount + trans.amount))
      }
    }
    sender ! TransferToHouse(homeAddress, trans.amount)
  }

  override def receive: Receive = {
    case GenerateAddress(x) => sender ! generateNewUserData(x)
    case ScheduleTrans() => scheduleTrans()
    case GenerateTransaction(addr) => generateTransaction(addr)
    case IncomingMoney(trans) => updateUserBalance(trans)
  }
}


