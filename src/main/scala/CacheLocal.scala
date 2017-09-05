package org.example.ecommercerecommendation

import grizzled.slf4j.Logger
import org.apache.predictionio.data.storage.Event

import com.github.blemale.scaffeine.{ Cache, Scaffeine }
import scala.concurrent.duration._

object LocalCache {
  val events = new LocalEvents(5000, 15.minutes)
  val items = new LocalItems(5000, 2.hours)
  val results = new LocalResults(1, 6.hours)
}

class LocalEvents(size: Long, ttl: Duration) {

  @transient lazy implicit val logger: Logger = Logger[this.type]

  val cache: Cache[String, Array[Event]] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ttl)
      .maximumSize(size)
      .build[String, Array[Event]]()

  def getFromCache(key: String): Option[Array[Event]] = {
    cache.getIfPresent(key)
  }

  def setInCache(key: String, value: Array[Event]) = {
    cache.put(key, value)
  }

}

class LocalItems(size: Long, ttl: Duration) {

  @transient lazy implicit val logger: Logger = Logger[this.type]

  val cache: Cache[String, Map[String, Any]] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ttl)
      .maximumSize(size)
      .build[String, Map[String, Any]]()

  def getFromCache(key: String): Option[Map[String, Any]] = {
    cache.getIfPresent(key)
  }

  def setInCache(key: String, value: Map[String, Any]) = {
    cache.put(key, value)
  }

}

class LocalResults(size: Long, ttl: Duration) {

  @transient lazy implicit val logger: Logger = Logger[this.type]

  val cache: Cache[String, Array[(Int, Double)]] =
    Scaffeine()
      .recordStats()
      .expireAfterWrite(ttl)
      .maximumSize(size)
      .build[String, Array[(Int, Double)]]()

  def get(key: String): Option[Array[(Int, Double)]] = {
    cache.getIfPresent(key)
  }

  def set(key: String, value: Array[(Int, Double)]) = {
    cache.put(key, value)
  }

}
