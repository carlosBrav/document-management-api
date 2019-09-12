package utils

import java.nio.ByteBuffer

import com.eaio.uuid.UUIDGen
import org.apache.commons.lang3.RandomStringUtils

class UniqueId {
  val clockSeqAndNode: Long = UUIDGen.getClockSeqAndNode
  val node: Array[Byte] = Array(
    ((clockSeqAndNode >> 40) & 0xff).asInstanceOf[Byte],
    ((clockSeqAndNode >> 32) & 0xff).asInstanceOf[Byte],
    ((clockSeqAndNode >> 24) & 0xff).asInstanceOf[Byte],
    ((clockSeqAndNode >> 16) & 0xff).asInstanceOf[Byte],
    ((clockSeqAndNode >> 8) & 0xff).asInstanceOf[Byte],
    ((clockSeqAndNode >> 0) & 0xff).asInstanceOf[Byte]
  )

  val tlbb: ThreadLocal[ByteBuffer] = new ThreadLocal[ByteBuffer]() {
    override  def initialValue(): ByteBuffer = ByteBuffer.allocate(16)
  }

  @volatile var seq: Int = 0
  @volatile var lastTimestamp: Long = System.currentTimeMillis()
  val lock = new Object()

  val maxShort: Int = 0xffff

  def getId: Array[Byte] = {
    if(seq == maxShort) {
      throw new RuntimeException("Too fast")
    }

    var time: Long = 0
    synchronized {
      time = System.currentTimeMillis()
      if(time != lastTimestamp) {
        lastTimestamp = time
        seq = 0
      }
      seq += 1
      val bb:ByteBuffer  = tlbb.get()
      bb.rewind()
      bb.putLong(time)
      bb.put(node)
      bb.putShort(seq.asInstanceOf[Short])
      bb.array()
    }
  }

  def getStringId: String = {
    val ba: Array[Byte] = getId
    val bb:ByteBuffer  = ByteBuffer.wrap(ba)
    val ts = bb.getLong()
    val node_0 = bb.getInt()
    val node_1 = bb.getShort()
    val seq = bb.getShort()
    f"$ts%013d${seq%100}%03d${UniqueId.randomMachineVhost}${RandomStringUtils.randomAlphabetic(3)}"
  }
}

object UniqueId {
  private val uniqueId = new UniqueId
  private val randomMachineVhost = RandomStringUtils.randomAlphabetic(1)

  def generateId: String = uniqueId.getStringId
}