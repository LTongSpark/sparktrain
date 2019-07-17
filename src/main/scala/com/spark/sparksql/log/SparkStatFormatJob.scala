package com.spark.sparksql.log

import org.apache.spark.sql.SparkSession

/**
  * @author LTong
  * @date 2019-06-17 上午 11:48
  */
object SparkStatFormatJob {
  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession.builder().appName("123").master("local[*]").getOrCreate()
    val data = spark.sparkContext.textFile("")
    data.map(line =>{
      val split = line.split(" ")
      val ip = split(0)
      val time = split(3) + split(4)
      val url = split(11).replaceAll("\"" ,"")
      val traffic = split(9)
      DateUtils.parse(time) + "\t" + url + "\t" + traffic + "\t" + ip
    }).saveAsTextFile("")
    spark.stop()
  }

}
