/**
  * 是每个要素标准化为以具有单位标准差和或0均值
  */

package com.spark.mlproject

import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.sql.SparkSession

object StandardScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("StandardScalerExample")
      .getOrCreate()

    val dataFrame = spark.read.format("libsvm").load("file:///D:/mr/spark-2.2.0-bin-hadoop2.7/data/mllib/sample_libsvm_data.txt")

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      //将数据缩放到单位标准差
      .setWithStd(true)
      //将数据中心为平均值
      .setWithMean(false)

    val scalerModel = scaler.fit(dataFrame)

    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show()

    spark.stop()
  }
}
