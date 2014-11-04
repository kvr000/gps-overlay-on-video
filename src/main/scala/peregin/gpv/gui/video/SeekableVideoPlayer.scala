package peregin.gpv.gui.video

import akka.actor.{Props, ActorSystem, Actor}

import peregin.gpv.model.Telemetry
import java.awt.Image
import peregin.gpv.util.{TimePrinter, Logging}


trait SeekableVideoPlayerFactory extends VideoPlayerFactory {
  override def createPlayer(url: String, telemetry: Telemetry, imageHandler: Image => Unit,
                            shiftHandler: () => Long, timeUpdater: (Long, Double) => Unit) =
    new SeekableVideoPlayer(url, telemetry, imageHandler, shiftHandler, timeUpdater)
}

class SeekableVideoPlayer(url: String, val telemetry: Telemetry,
                         imageHandler: Image => Unit, shiftHandler: () => Long,
                         timeUpdater: (Long, Double) => Unit) extends VideoPlayer with DashboardPainter with Logging {

  val video = new SeekableVideoStream(url)

  override def play() {
    playerActor ! Play
  }


  override def step() {
    playerActor ! Play
  }

  override def pause() = sys.error("not supported")

  override def seek(percentage: Double) {
    playerActor ! Seek(percentage)
  }

  override def close() {
    video.close()
  }


  override def duration = video.durationInMillis

  // notifier from the actor
  private[video] def handleFrame(frame: FrameIsReady): Unit = {
    paintGauges(telemetry, frame.tsInMillis, frame.image, shiftHandler())
    timeUpdater(frame.tsInMillis, frame.percentage)
    imageHandler(frame.image)
  }


  val system = ActorSystem("gpv")
  val playerActor = system.actorOf(Props(new PlayerControllerActor(video, handleFrame)), name = "playerController")
  playerActor ! Play
}

sealed trait PlayerCommand
case object Play extends PlayerCommand
case class Seek(percentage: Double) extends PlayerCommand

class PlayerControllerActor(video: SeekableVideoStream, listener: (FrameIsReady) => Unit) extends Actor with Logging {

  override def receive = {
    case Play => video.readPacket.head match {
      case frame @ FrameIsReady(tsInMillis, percentage, keyFrame, image) =>
        info(f"frame received, ts=${TimePrinter.printDuration(tsInMillis)}, @=$percentage%2.2f, keyFrame=$keyFrame")
        if (tsInMillis > 0) video.waitIfNeeded(tsInMillis)
        listener(frame)
        //self ! Play
      case EndOfStream => context.stop(self)
      case ReadInProgress => self ! Play
      case _ => // ignore
    }
    case Seek(percentage) =>
      video.seek(percentage).foreach{ seekFrame =>
        info(f"seek frame received, ts=${TimePrinter.printDuration(seekFrame.tsInMillis)}, @=${seekFrame.percentage%2.2f}")
        listener(seekFrame)
      }
      video.reset()
    case _ => // do nothing
  }
}