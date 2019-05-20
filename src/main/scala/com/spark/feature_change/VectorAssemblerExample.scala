
package com.spark.feature_change


import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * 是将给定的一系列的列合并到单个向量列中的transformer，可以将原始特征和不同的特征转换器生成的特征
  * 合并为单个特征向量，来训练mr模型，
  */
object VectorAssemblerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("VectorAssemblerExample")
      .getOrCreate()


    val dataset = spark.createDataFrame(
      Seq((0, 18, 1.0, Vectors.dense(0.0, 10.0, 0.5), 1.0))
    ).toDF("id", "hour", "mobile", "userFeatures", "clicked")

    val assembler = new VectorAssembler()
      .setInputCols(Array("hour", "mobile", "userFeatures"))
      .setOutputCol("features")

    val output = assembler.transform(dataset)
    output.select("features", "clicked").show(false)

  }
}
