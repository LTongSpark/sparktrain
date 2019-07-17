package com.spark.sparksql.data

import org.apache.spark.sql.SparkSession

/**
  * @author LTong
  * @date 2019-06-17 上午 11:03
  */
object DataSet {
  case class Sales(transactionId: Int, customerId: Int, itemId: Int, amountPaid: Double)
  def main(args: Array[String]): Unit = {
    val spark:SparkSession  = SparkSession.builder().appName(this.getClass.getSimpleName).master("local[8]").getOrCreate()

    val path = ""

    val data = spark.read.option("head" ,true).option("inferSchema" ,true).csv(path)

    import spark.implicits._
    val ds = data.as[Sales]
    ds.show()
  }

}
