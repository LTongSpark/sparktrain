
package com.spark.feature_change

import org.apache.spark.ml.feature.Tokenizer
import org.apache.spark.sql.SparkSession

/**是将文本拆分为单词的过程
  *
  */
object TokenizerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("TokenizerExample")
      .getOrCreate()

    val sentenceDataFrame = spark.createDataFrame(Seq(
      (0, "Hi I heard about Spark"),
      (1, "I wish Java could use case classes"),
      (2, "Logistic,regression,models,are,neat")
    )).toDF("id", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val tokenized = tokenizer.transform(sentenceDataFrame)
    tokenized.show(false)

  }
}
