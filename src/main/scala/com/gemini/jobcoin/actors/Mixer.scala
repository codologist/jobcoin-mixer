package com.gemini.jobcoin.actors

import java.util.UUID
import akka.actor.{Actor, ActorLogging}
import com.gemini.jobcoin.actors.Mixer._
import com.gemini.jobcoin.actors.Transactioner.{TransferMoney, TransferToHouse}

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
  case class IncomingMoney(addr: String, amount: BigDecimal)
}
class Mixer extends Actor with ActorLogging{
  private val homeAddress = "mixer-1983-8260-2786-2232"
  @volatile private var stateMap: Map[String, UserState] = Map.empty
  def generateNewUserData(x: InputAddresses) : String = {
    this.synchronized {
      val addrTo = UUID.randomUUID().toString
      val newUser = UserState(x, BigDecimal(0), BigDecimal(0), addrTo, 0.1F, BigDecimal(0))
      stateMap += (addrTo -> newUser)
      addrTo
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
        val amtToTransfer = x.amountLeft * BigDecimal(amtPct)
        // Charge money if they hold a balance of more than million dollars
        val fee = x.amountLeft > BigDecimal(1000000) match {
          case true => amtToTransfer * BigDecimal(x.pctfee)
          case false => BigDecimal(0)
        }
        val randIndex = Random.nextInt(x.transferTos.addresses.length)
        sender ! TransferMoney(Transaction(x.transferTos.addresses(randIndex),amtToTransfer,fee))
      }
    }
  }

  def updateUserBalance(addr: String, amt: BigDecimal) = {
    this.synchronized {
      stateMap.get(addr) match {
        case None => throw new IllegalArgumentException("Could not find user for which money was send")
        case Some(x) => stateMap += (addr -> x.copy(totalAmount = x.totalAmount + amt))
      }
    }
    sender ! TransferToHouse(homeAddress, amt)
  }

  override def receive: Receive = {
    case GenerateAddress(x) => generateNewUserData(x)
    case ScheduleTrans() => scheduleTrans()
    case GenerateTransaction(addr) => generateTransaction(addr)
    case IncomingMoney(addr, amt) => updateUserBalance(addr, amt)
  }
}


