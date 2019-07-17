package com.spark.sparksql.log

import org.apache.spark.sql.{SaveMode, SparkSession}

/**
  * @author LTong
  * @date 2019-06-17 上午 11:34
  */
object sparkStatCleanJob {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("SparkStatCleanJob").master("local[2]").getOrCreate()

    val data = spark.sparkContext.textFile("file:///")

    val accessDF = spark.createDataFrame(data.map(line =>AccessConvertUtil.parse(line)), AccessConvertUtil.struct)

    accessDF.coalesce(1).write.format("parquet")
      .mode(SaveMode.Overwrite)
      //按天进行分区时实现
      .partitionBy("day")
      .save("")
  }

}
