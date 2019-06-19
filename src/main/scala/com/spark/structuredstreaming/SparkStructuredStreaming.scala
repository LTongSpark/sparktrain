package com.spark.structuredstreaming

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.OutputMode

/**
  * @author LTong
  * @date 2019-05-23 10:36
  *       {"a":"1","b":"2","c":"2018-01-08","d":[23.9,45]}
  */
object SparkStructuredStreaming {
  def main(args: Array[String]): Unit = {
    //创建sparkSession
    val spark = SparkSession.builder().master("local[*]").appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    import  spark.implicits._
      val df = spark.readStream.format("kafka")
        .option("kafka.bootstrap.servers" ,"zytc222:9092,zytc223:9092,zytc224:9092")
        .option("subscribe" ,"yc-info")
        .option("startingOffsets", "latest")
        .option("max.poll.records", 10000)
        .option("failOnDataLoss","false")
        .load()

    val word = df.selectExpr("CAST(key AS STRING)" ,"CAST(value AS STRING)").as[(String ,String)]
    val q = word.select("*").writeStream.queryName("kafka_test").outputMode(OutputMode.Append())
      .format("console")
        .start()
    q.awaitTermination()
  }

}
