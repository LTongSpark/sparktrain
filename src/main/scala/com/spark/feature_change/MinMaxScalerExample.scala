
package com.spark.feature_change

/**
  *
  * MinMaxScaler转换由向量行组成的数据集,将每个特征调整到一个特定的范围(通常是[0,1])。它有下面两个参数:
  *
  * min:默认是0。转换的下界,被所有的特征共享。
  * max:默认是1。转换的上界,被所有特征共享。
  *   MinMaxScaler计算数据集上的概要统计数据,产生一个MinMaxScalerModel。然后就可以用这个模型单独的转换每个特征到特定的范围。
  */

import org.apache.spark.ml.feature.MinMaxScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

object MinMaxScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("MinMaxScalerExample")
      .getOrCreate()

    val dataFrame = spark.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.1, -1.0)),
      (1, Vectors.dense(2.0, 1.1, 1.0)),
      (2, Vectors.dense(3.0, 10.1, 3.0))
    )).toDF("id", "features")

    val scaler = new MinMaxScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")

    val scalerModel = scaler.fit(dataFrame)

    val scaledData = scalerModel.transform(dataFrame)
    scaledData.select("features", "scaledFeatures").show()
  }
}
