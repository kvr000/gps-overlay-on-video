package peregin.gpv.model

import org.joda.time.DateTime
import org.jxmapviewer.viewer.GeoPosition

object Sonda {

  def empty: Sonda = zeroAt(new DateTime(0))

  def zeroAt(t: DateTime): Sonda = new Sonda(t, InputValue.zero,
    new GeoPosition(0, 0),
    InputValue.empty, InputValue.empty,
    InputValue.empty, InputValue.empty, InputValue.empty,
    InputValue.empty, InputValue.empty, InputValue.empty, InputValue.empty
  )

  def sample(): Sonda = new Sonda(
    time = DateTime.now(), elapsedTime = InputValue.zero,
    location = new GeoPosition(47.366074, 8.541264), // Buerkliplatz, Zurich, Switzerland
    elevation = InputValue(Some(480), MinMax.max(640)), grade = InputValue.empty,
    distance = InputValue(Some(4), MinMax.max(12)),
    speed = InputValue(Some(32), MinMax.max(61)),
    bearing = InputValue(Some(90), MinMax.max(360)),
    cadence = InputValue(Some(81), MinMax.max(100)),
    heartRate = InputValue(Some(110), MinMax.max(160)),
    power = InputValue(Some(223), MinMax.max(320)),
    temperature = InputValue(Some(30), MinMax.max(100))
  )
}

case class Sonda(time: DateTime, elapsedTime: InputValue,
                 location: GeoPosition,
                 elevation: InputValue, grade: InputValue,
                 distance: InputValue, speed: InputValue, bearing: InputValue,
                 cadence: InputValue, heartRate: InputValue, power: InputValue,
                 temperature: InputValue) {

  // just for debugging purposes
  private var trackIndex: Int = 0
  private var videoProgressInMillis = 0L 

  def withTrackIndex(v: Int): Sonda = {
    trackIndex = v
    this
  }
  def getTrackIndex: Int = trackIndex
  
  def videoProgress_= (progressInMillis: Long): Unit = videoProgressInMillis = progressInMillis
  def videoProgress: Long = videoProgressInMillis
}
