package com.sparksql.data

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

/**
  * @author LTong
  * @date 2019-06-17 上午 10:52
  */
object DataFrameRDDApp {
  case  class Info(age:Int ,name:String)
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("DataFrameRDDApp").master("local[2]").getOrCreate()

    /**
      * rdd =>dataframe ,采用反射的方式
      */

    val rdd = spark.sparkContext.textFile("")

    import spark.implicits._
    val studentDF:DataFrame = rdd.map(_.split("\t")).map(line =>Info(line(0).toInt ,line(1))).toDF()

    studentDF.printSchema()
    //注册成临时表
    studentDF.createTempView("info")
    spark.sql("select * from info").show()



    /**
      * rdd =>dataframe,采用编程的方式
      */
    val student: RDD[Row] = rdd.map(_.split("\t")).map(line => Row(line(0).toInt, line(1)))

    val schema = StructType(Array(
      StructField("id",IntegerType ,true),
      StructField("name",StringType ,true)
    ))

    val data:DataFrame = spark.createDataFrame(student,schema)

    data.show()

  }

}
