
package com.spark.feature_change

import org.apache.spark.ml.feature.DCT
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * 是将时域的n维实例序列转换成频域的n维实例的过程（有点类似于离散傅里叶变换）
  * DCT类提供了离散余弦变换DCT-II的功能，将离散余弦变换后的的结果乘以得到一个与时域矩阵长度一致的矩阵，
  * 没有偏移被应用于变换的序列
  */
object DCTExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("DCTExample")
      .getOrCreate()

    val data = Seq(
      Vectors.dense(0.0, 1.0, -2.0, 3.0),
      Vectors.dense(-1.0, 2.0, 4.0, -7.0),
      Vectors.dense(14.0, -2.0, -5.0, 1.0))

    val df = spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")

    val dct = new DCT()
      .setInputCol("features")
      .setOutputCol("featuresDCT")
      .setInverse(false)

    val dctDf = dct.transform(df)
    dctDf.show(false)
  }
}

