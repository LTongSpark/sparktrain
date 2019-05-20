package com.spark.mlproject


import org.apache.spark.ml.feature.Word2Vec
import org.apache.spark.sql.SparkSession

/**
  *
  * 特征抽取_Word2Vec
  * @author LTong
  * @date 2019-05-16 15:20
  */
object _Word2Vec {
  def main(args: Array[String]): Unit = {
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
