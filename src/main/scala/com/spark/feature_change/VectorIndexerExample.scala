
package com.spark.feature_change

import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.sql.SparkSession

/**
  * 是将指定向量数据集中的分类离散化，可以自动的确定哪些特征是离散的，并将原始值转换为离散索引
  *    VectorIndexer可以自动将决定哪些features是类别的，并且可以将原始值转换成类别指标。
  *    而在决策树和提升树算法中，能够确定种类特征指标，VectorIndexer可以提升这些算法的性能。
  */
object VectorIndexerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("VectorIndexerExample")
      .getOrCreate()

    val data = spark.read.format("libsvm").load("D:/mr/spark-2.2.0-bin-hadoop2.7/data/mllib/sample_libsvm_data.txt")

    val indexer = new VectorIndexer()
      .setInputCol("features")
      .setOutputCol("indexed")
      .setMaxCategories(10)
    val indexerModel = indexer.fit(data)
    val indexedData = indexerModel.transform(data)
    indexedData.show(false)

  }
}
