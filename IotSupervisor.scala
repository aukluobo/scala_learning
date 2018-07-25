package com.lightbend.akka.sample

import akka.actor.{Actor,ActorSystem,ActorLogging,Props}
import scala.io.StdIn

object IotSupervisor {
    def props():Props = Props(new IotSupervisor)
}

class IotSupervisor extends Actor with ActorLogging {
    override def preStart():Unit = log.info("Iot Application started")
    override def postStop():Unit = log.info("Iot Application stopped")

    override def receive:Receive = Actor.emptyBehavior
}

object Device{
    def props(groupId:String,deviceId:String):Props = Props(new Device(groupId,deviceId))

    final case class recordTemperature(requestId:Long,value:Double)
    final case class temperatureRecorded(requestId:Long)
    final case class readTemperature(requestId:Long)
    final case class respondTemperature(requestId:Long,value:Option[Double])
}

class Device(groupId:String,deviceId:String) extends Actor with ActorLogging {
    import Device._

    var lastTemperatureReading:Option[Double] = None
    override def preStart():Unit = log.info("Device actor {}-{} started",groupId,deviceId)
    override def postStop():Unit = log.info("Device actor {}-{} stopped",groupId,deviceId)
    override def receive:Receive = {
        case deviceManager.requestTrackDevice(`groupId`,`deviceId`) => 
            sender() ! deviceManager.deviceRegistered
        case deviceManager.requestTrackDevice(groupId,deviceId) =>
            log.warning(
                "Ignoring TrackDevice request for {}-{}. This actor is responsible for {}-{}",groupId,deviceId,this.groupId,this.deviceId
            )
        case recordTemperature(id,value) =>{
            log.info("Recorded temperature reading {} with {}",id,value)
            lastTemperatureReading=Some(value)
            sender() ! temperatureRecorded(id)
        }
        case readTemperature(id) => 
            sender() ! respondTemperature(id,lastTemperatureReading)
    }
}

object DeviceGroup{
    def props(groupId:String):Props = Props(new DeviceGroup(groupId))
    
    final case class requestDeviceList(requestId:Long)
    final case class replyDevicedList(requestId:Long,ids:Set[String])

    final case class requestAllTemperatures(requestId:Long)
    final case class respondAllTemperatures(requestId:Long,temperatures:Map[String,temperatureReading])

    sealed trait temperatureReading
    final case class temperature(value:Double) extends temperatureReading
    case object temperatureNotAvailable extends temperatureReading
    case object deviceNotAvailable extends temperatureReading
    case object deviceTimeOut extends temperatureReading
}
class DeviceGroup(groupId:String) extends Actor with ActorLogging {
    var deviceIdToActor = Map.empty[String,ActorRef]
    var actorToDeviceId = Map.empty[ActorRef,String]

    override def preStart():Unit = log.info("DeviceGroup {} started",groupId)
    override def postStop():Unit = log.info("DeviceGroup {} stopped",groupId)
    override def receive:Receive = {
        case trackMsg @ requestTrackDevice(`groupId`,_) =>
            deviceIdToActor.get(trackMsg.deviceId) match {
                case Some(deviceActor) => deviceActor forward trackMsg
                case None => {
                    log.info("Creating device actor for {}",trackMsg.deviceId)
                    val deviceActor = context.actorOf(Device.props(groupId,trackMsg.deviceId),s"device-${trackMsg.deviceId}")
                    context.watch(deviceActor)
                    actorToDeviceId += deviceActor->trackMsg.deviceId
                    deviceIdToActor += trackMsg.deviceId -> deviceActor
                    deviceActor forward trackMsg
                }
            }
        case requestTrackDevice(groupId,deviceId) => {
            log.warning("Ignoring TrackDevice request for {}. This actor is responsible for {}",groupId,this.groupId)
        }
        case requestDeviceList(requestId) => sender() ! replyDevicedList(requestId,deviceIdToActor.keySet)
        case Terminated(deviceActor) => {
            val deviceId = actorToDeviceId(deviceActor)
            log.info("Device actor for {} has been terminated",deviceId)
            actorToDeviceId -= deviceActor
            deviceIdToActor -= deviceId
        }
    }
}

object deviceManager{
    def props():Props = Props(new deviceManager)

    fina case class requestTrackDevice(groupId:String,deviceId:String)
    case object deviceRegistered
}
class deviceManager extends Actor with ActorLogging {
    var groupIdToActor = Map.empty[String,ActorRef]
    var actorToGroupId = Map.empty[ActorRef,String]

    override def preStart():Unit = log.info("DeviceManager started")
    override def postStop():Unit = log.info("DeviceManager stopped")

    override def receive:Receive = {
        case trackMsg @ requestTrackDevice(groupId,_) =>{
            groupIdToActor.get(groupId) match {
                case Some(ref) => ref forward trackMsg
                case None => {
                    log.info("Creating device group actor for {}", groupId)
                    val groupActor = context.actorOf(DeviceGroup.props(groupId),"group-"+groupId)
                    context.watch(groupActor)
                    groupIdToActor += groupId -> groupActor
                    actorToGroupId += groupActor->groupId
                }
            }
        }
        case Terminated(groupActor) => {
            val groupId = actorToGroupId(groupActor)
            log.info("Device group actor for {} has been terminated",groupId)
            actorToGroupId -= groupActor
            groupIdToActor -= groupId
        }
    }

}

object IotApp {
    def main(args:Array[String]):Unit={
        val system = ActorSystem("iot-system")

        try {
            val supervisor = system.actorOf(IotSupervisor.props(),"iot-supervisor")
            StdIn.readLine()
        }
        finally{
            system.terminate()
        }

    }
}


