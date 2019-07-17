package com.spark.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * @author LTong
  * @date 2019-06-19 下午 3:55
  */
object pv_uv {
  def main(args: Array[String]): Unit = {
    // 10.12.133.145,post,/app2/index.xml
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName(this.getClass.getSimpleName).getOrCreate()
    import spark.implicits._
    val data: RDD[String] = spark.sparkContext.textFile("file:///D:/mr/qwe.txt")
    val pvuv_data:DataFrame = data.map(rdd =>{
      val attr: Array[String] = rdd.split(",")
      val app:String = attr(2).split("/")(1)
      (attr(0) ,app)
    }).toDF("ip" ,"app")

    //统计uv pv
    import org.apache.spark.sql.functions._
   val pvuv:DataFrame =  pvuv_data.groupBy("app").agg(count("ip").as("pv") ,
     countDistinct("ip").as("uv"))
    pvuv.show(false)


  }

}
