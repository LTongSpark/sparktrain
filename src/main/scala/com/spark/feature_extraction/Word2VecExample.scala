
package com.spark.feature_extraction

import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.SparkSession

/**
  * Word2Vec是一个评估器，由表示文档的单词序列训练而成的一个Word2VecModel。
  * 模型映射每个单词为一个唯一固定大小的向量。Word2VecModel使用文档中所有单词的平均值将每个文档转换成向量，
  * 这个向量可以作为预测的特征，文档相似性计算等等
  *
  */
object Word2VecExample {
  def main(args: Array[String]) {
    val spark=SparkSession.builder().master("local").appName("Word2Vec").getOrCreate()
    import spark.implicits._

    val documentDF= spark.createDataFrame(Seq(
      "soyo like spark and hadoop".split(" "),
      "scala is good tool to study".split(" "),
      "but java i want to study and spark".split(" "),
      "soyo like spark and hadoop ".split(" ")
    ).map(Tuple1.apply)).toDF("text")
    val word2Vec=new Word2Vec().setInputCol("text").setOutputCol("result").setVectorSize(5).setMinCount(0)  //设置特征向量维数为5
    val word2Vec_model=word2Vec.fit(documentDF)  //训练模型
    val result=word2Vec_model.transform(documentDF) //把文档转换成特征向量
    result.show(false)
  }
}
