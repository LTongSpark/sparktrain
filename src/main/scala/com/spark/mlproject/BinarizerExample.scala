
package com.spark.mlproject

import org.apache.spark.ml.feature.Binarizer
import org.apache.spark.sql.{SparkSession}

/**
  * 连续型数据处理之二值化
  */
object BinarizerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("BinarizerExample")
      .master("local[*]")
      .getOrCreate()
    val data = Array((0, 0.1), (1, 0.8), (2, 0.2))
    val dataFrame = spark.createDataFrame(data).toDF("id", "feature")

    val binarizer: Binarizer = new Binarizer()
      .setInputCol("feature")
      .setOutputCol("binarized_feature")
      //根据0.5来分类别
      .setThreshold(0.5)

    val binarizedDataFrame = binarizer.transform(dataFrame)

    println(s"Binarizer output with Threshold = ${binarizer.getThreshold}")
    binarizedDataFrame.show()

    spark.stop()
  }
}
