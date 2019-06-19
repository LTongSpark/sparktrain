package com.sparksql

import com.sparksql.log.dao.StatDao
import com.sparksql.log.entity.{DayCityVideoAccessStat, DayVideoAccessStat, DayVideoTrafficsStat}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ListBuffer

/**
  * @author LTong
  * @date 2019-06-17 下午 12:36
  */
object TopNStatJob {
  def main(args: Array[String]): Unit = {

    if(args.length != 2){
      print("Usage:TopNStatJobYARN <inputPath> <day>")
      System.exit(1)
    }

    val Array(inputPath ,day) = args

    val spark = SparkSession.builder().appName("TopNStatJob")
      .config("spark.sql.sources.partitionColumnTypeInference.enabled", "false").getOrCreate()

    //加载数据
    val accessDF = spark.read.parquet(inputPath)

    //清空数据表
    StatDao.deleteData(day)
    //最受欢迎的TopN课程
    videoAccessTopNStat(spark, accessDF ,day)
    //按城市进行统计
    cityAccessTopNStat(spark, accessDF,day)
    //按流量进行统计
    videoTrafficsTopNStat(spark, accessDF ,day)
  spark.stop()
  }

  def videoAccessTopNStat(spark:SparkSession ,accessDF:DataFrame ,day:String): Unit ={
    /**
      * 使用dataframe进行统计
      */

    import spark.implicits._
    val videoAccessTopN = accessDF.filter($"day" === day && $"cmsType" === "video").groupBy("day","cmsId")
      .agg(count("cusId").as("times"))
      .orderBy($"times".desc)


    //保存数据到mysql

    videoAccessTopN.foreachPartition(rdd =>{
      var list = new ListBuffer[DayVideoAccessStat]

      rdd.foreach(info =>{
        val day = info.getAs[String]("day")
        val cmsId = info.getAs[Long]("cmsId")
        val times = info.getAs[Long]("times")

        list.append(DayVideoAccessStat(day,cmsId,times))

      })
      StatDao.insertDayVideoAccessTopN(list)
    })
  }

  /**
    * 按照地市进行统计TopN课程
    *
    * @param spark
    * @param commonDF
    */
  def cityAccessTopNStat(spark: SparkSession, accessDF: DataFrame ,day :String): Unit = {
    import spark.implicits._
    val cityAccessTopNDF = accessDF.filter($"day" === day && $"cmsType" === "video").groupBy("day", "city", "cmsId")
      .agg(count("cmsId").as("times"))

    //    cityAccessTopNDF.show(false)

    val top3DF = cityAccessTopNDF.select(
      cityAccessTopNDF("day"),
      cityAccessTopNDF("city"),
      cityAccessTopNDF("cmsId"),
      cityAccessTopNDF("times"),
      row_number().over(Window.partitionBy(cityAccessTopNDF("city"))
        .orderBy(cityAccessTopNDF("times").desc)).as("times_rank")
    ).filter("times_rank <= 3")

    try {
      top3DF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayCityVideoAccessStat]

        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val cmsId = info.getAs[Long]("cmsId")
          val city = info.getAs[String]("city")
          val times = info.getAs[Long]("times")
          val timesRank = info.getAs[Int]("times_rank")
          list.append(DayCityVideoAccessStat(day, cmsId, city, times, timesRank))
        })
        StatDao.insertDayCityVideoAccessTopN(list)
      })
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

  /**
    * 按照流量进行统计
    *
    * @param spark
    * @param commonDF
    */
  def videoTrafficsTopNStat(spark: SparkSession, accessDF: DataFrame ,day:String): Unit = {
    import spark.implicits._
    val trafficsAccessTopNDF = accessDF.filter($"day" === day && $"cmsType" === "video").groupBy("day", "cmsId")
      .agg(sum("traffic").as("traffics")).orderBy($"traffics".desc)

    try {
      trafficsAccessTopNDF.foreachPartition(partitionOfRecords => {
        val list = new ListBuffer[DayVideoTrafficsStat]
        partitionOfRecords.foreach(info => {
          val day = info.getAs[String]("day")
          val cmsId = info.getAs[Long]("cmsId")
          val traffics = info.getAs[Long]("traffics")
          list.append(DayVideoTrafficsStat(day, cmsId, traffics))
        })
        StatDao.insertDayVideoTrafficsAccessTopN(list)
      })
    } catch {
      case e: Exception => e.printStackTrace()
    }


  }



}
