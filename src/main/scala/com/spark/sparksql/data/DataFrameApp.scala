package com.spark.sparksql.data

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @author LTong
  * @date 2019-06-17 上午 10:07
  */
object DataFrameApp {
  def main(args: Array[String]): Unit = {
    //创建SparkSession

    val spark :SparkSession= SparkSession.builder().appName("DataFrameApp").master("local[*]").getOrCreate()

    //将json文件加载成一个dataframe
    val frame: DataFrame = spark.read.format("json").load("D:\\mobsf\\sparktrain\\src\\main\\scala\\text\\test.json")

    //输出dataframe的scheam信息
    frame.printSchema()

    //输出数据的前20行
    frame.show(20)

    //查询指定的列
    frame.select("tong").show()

    //查询指定的列并计算
    import org.apache.spark.sql.functions._
    frame.select((frame.col("age") + 10).as("age2")).show()

    frame.filter(frame.col("age") > 10).show()

    //分组求和

    frame.groupBy("age").agg(count("age").as("count1")).show()


  }

}
