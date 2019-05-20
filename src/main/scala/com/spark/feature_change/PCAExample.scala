

package com.spark.feature_change

/**
  * pca 是使用正交变换将可能相关变量的一组观察值转换为称为主成分的线性不相关的值得一组统计过程。将向量投影到低纬度空间的模型
  */

import org.apache.spark.ml.feature.PCA
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

object PCAExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("PCAExample")
      .getOrCreate()

    val data = Array(
      Vectors.sparse(5, Seq((1, 1.0), (3, 7.0))),
      Vectors.dense(2.0, 0.0, 3.0, 4.0, 5.0),
      Vectors.dense(4.0, 0.0, 0.0, 6.0, 7.0)
    )
    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")

    val pca = new PCA()
      .setInputCol("features")
      .setOutputCol("pcaFeatures")
      .setK(3)
      .fit(df)

    val result = pca.transform(df)
    result.show(false)

    spark.stop()
  }
}
