package com.spark.feature_change

import org.apache.spark.ml.feature.MaxAbsScaler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * MaxAbsScaler转换由向量列组成的数据集,将每个特征调整到[-1,1]的范围,它通过每个特征内的最大绝对值来划分。
  * 它不会移动和聚集数据,因此不会破坏任何的稀疏性。
  *
  * MaxAbsScaler计算数据集上的统计数据,生成MaxAbsScalerModel,然后使用生成的模型分别的转换特征到范围[-1,1]
  */
object MaxAbsScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("MaxAbsScalerExample")
      .getOrCreate()

    val dataFrame = spark.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 0.1, -8.0)),
      (1, Vectors.dense(2.0, 1.0, -4.0)),
      (2, Vectors.dense(4.0, 10.0, 8.0))
    )).toDF("id", "features")

    val scaler = new MaxAbsScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")

    val scalerModel = scaler.fit(dataFrame)

    val scaledData = scalerModel.transform(dataFrame)
    scaledData.select("features", "scaledFeatures").show()
    // $example off$

    spark.stop()
  }
}
