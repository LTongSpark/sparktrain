package com.spark.spark_redis

import com.spark.spark_redis.util.JPools
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Assign
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * @author LTong
  * @date 19-6-12 12:29
  */
object spark_redis {

  def main(args: Array[String]): Unit = {
    //val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}").getOrCreate()
    val conf = new SparkConf().setMaster("local[*]").setAppName(this.getClass.getSimpleName)

    val ssc: StreamingContext = new StreamingContext(conf, Seconds(2))
    val groupId = "streaming1"
    //kafka参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "zytc222:9092,zytc223:9092,zytc224:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val topics = Array("yc-info")


    //从redis中获取偏移量
    val offsetMap = Map[TopicPartition, Long]()

    //链接到kafka的数据源
    val stream:InputDStream[ConsumerRecord[String, String]] = if(offsetMap.size == 0){
      KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Subscribe[String, String](topics, kafkaParams))
    }else{
      KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent,
        Assign[String, String](offsetMap.keys ,kafkaParams,offsetMap))
    }
    //创建在master节点的Driver
    stream.foreachRDD(rdd =>{
      //判断rdd非空
      if(!rdd.isEmpty()) {

        val offsetRange: Array[OffsetRange] = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        val reduced = rdd.map(x => (x.value(),1)).reduceByKey(_+_)
        reduced.foreach(println)
        //写逻辑处理
        // TODO:
        //将偏移量存到redis
        val jedis = JPools.getJedis
        for (offset <- offsetRange) {
          //  yc-info-0
          jedis.hset(groupId, offset.topic + "-" + offset.partition, offset.untilOffset.toString)
        }
      }
    })
    ssc.start()
    ssc.awaitTermination()
  }

}
