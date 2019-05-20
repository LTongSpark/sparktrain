
package com.spark.feature_extraction

import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}
import org.apache.spark.sql.SparkSession

/**
  * CountVectorizer和CountVectorizerModel的目标是帮助将一个文档集合转换成一个包含token计数的向量当没有预先的字典可用时。
  * CountVectorizer可用作估计器来提取词汇表，生成CountVectorizerModel。该模型为文档中的文档生成稀疏的词汇表示形式，
  * 然后将这些文档传递给LDA等其他算法。
  * 在拟合过程中，CountVectorizer将在语料库中选择由词频排序最高的词汇。
  * 一个可选的参数minDF也通过指定一个词汇必须出现在词汇表中的最小值(或小于1.0)来影响拟合过程。
  * 另一个可选的二进制切换参数控制输出向量。如果设置为真，所有非零计数都设置为1。这对于离散的概率模型来说尤其有用，
  * 模型是二进制的，而不是整数的。
  *
  */
object CountVectorizerExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("CountVectorizerExample")
      .getOrCreate()

    val df = spark.createDataFrame(Seq(
      (0, Array("a", "b", "c")),
      (1, Array("a", "b", "b", "c", "a"))
    )).toDF("id", "words")

    val cvModel: CountVectorizerModel = new CountVectorizer()
      .setInputCol("words")
      .setOutputCol("features")
      .setVocabSize(3)
      .setMinDF(2)
      .fit(df)

    val cvm = new CountVectorizerModel(Array("a", "b", "c"))
      .setInputCol("words")
      .setOutputCol("features")

    cvModel.transform(df).show(false)

    spark.stop()
  }
}


