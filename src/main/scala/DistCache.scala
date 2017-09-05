package org.example.ecommercerecommendation


import org.apache.commons.codec.digest.DigestUtils
import org.apache.predictionio.data.storage.Event

import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

import scala.language.{ implicitConversions, postfixOps }
import grizzled.slf4j.Logger

import org.json4s._
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{ read, write }

import com.redis._

object DistCache {

  val redis = new RedisClientPool("127.0.0.1", 6379)
  //  val redis = new RedisClientPool("10.56.34.7", 6379)
  //  val redis2 = new RedisClient("10.56.34.7", 6379)
  //  val redis3 = new RedisClient("10.56.34.7", 6379)

  val event = new DistEvents(redis)
  val items = new DistItems(redis)
  val results = new DistResults(redis)

}

class DistEvents(clientPool: RedisClientPool) {

  implicit val formats = Serialization.formats(NoTypeHints) ++ org.json4s.ext.JodaTimeSerializers.all

  @transient lazy implicit val logger: Logger = Logger[this.type]

  def serialize(obj: Array[Event]): String = {
    write(obj)
  }

  def deserialize(value: String): Array[Event] = {
    read[Array[Event]](value)
  }

  def getFromDistCache(key: String): Option[Array[Event]] = {
    try {
      Option(deserialize(clientPool.withClient { client =>
        client.get[String](key).get
      }))
    } catch {
      case e: Exception =>
        logger.warn(s"Error getting cached events ${e.getMessage}")
        None
    }
  }

  def setInDistCache(key: String, value: Array[Event], ttl: Duration) = {
    clientPool.withClient { client =>
      client.set(key, serialize(value))
    }
  }
}

class DistItems(clientPool: RedisClientPool) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ org.json4s.ext.JodaTimeSerializers.all

  @transient lazy implicit val logger: Logger = Logger[this.type]

  def serialize(obj: Map[String, Any]): String = {
    write(obj)
  }

  def deserialize(value: String): Map[String, Any] = {
    read[Map[String, Any]](value)
  }

  def getFromDistCache(key: String): Option[Map[String, Any]] = {
    try {
      Option(deserialize(clientPool.withClient { client =>
        client.get[String](key).get
      }))
    } catch {
      case e: Exception =>
        logger.warn(s"Error getting cached item ${e.getMessage}")
        None
    }
  }

  def setInDistCache(key: String, value: Map[String, Any], ttl: Duration) = {
    clientPool.withClient { client =>
      client.set(key, serialize(value))
    }
  }

}

class DistResults(clientPool: RedisClientPool) {
  implicit val formats = Serialization.formats(NoTypeHints) ++ org.json4s.ext.JodaTimeSerializers.all

  @transient lazy implicit val logger: Logger = Logger[this.type]

  def serialize(obj: Array[ItemScore]): String = {
    write(obj)
  }

  def deserialize(value: String): Array[ItemScore] = {
    read[Array[ItemScore]](value)
  }

  def get(key: String): Option[Array[ItemScore]] = {
    try {
      Option(deserialize(clientPool.withClient { client =>
        client.get[String](key).get
      }))
    } catch {
      case e: Exception =>
        logger.warn(s"Error getting cached results ${e.getMessage}")
        None
    }
  }

  def set(key: String, value: Array[ItemScore], ttl: Duration) = {
    clientPool.withClient { client =>
      client.set(key, serialize(value))
    }
  }

  def hash(value: String) = DigestUtils.md5Hex(value)

}

