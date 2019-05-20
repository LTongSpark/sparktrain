package com.spark.mlproject
import org.apache.spark.ml.feature.CountVectorizer
import org.apache.spark.sql.SparkSession
/**
  *
  * 文本特征提取->> CountVectorizer:基于词频数的文档向量
  * @author LTong
  * @date 2019-05-16 15:11
  */
object CountVector {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()
    val df= spark.createDataFrame(Seq(
      (0,Array("soyo","spark","soyo2","soyo","8")),
      (1,Array("soyo","hadoop","soyo","hadoop","xiaozhou","soyo2","spark","8","8")),
      (2,Array("soyo","spark","soyo2","hadoop","soyo3","8")),
      (3,Array("soyo","spark","soyo20","hadoop","soyo2","8","8")),
      (4,Array("soyo","8","spark","8","spark","spark","8"))
    )).toDF("id","words")

    //设置词汇表的最大个数为3,在5个文档中出现
    val model = new CountVectorizer().setInputCol("words").setOutputCol("features").setVocabSize(3)
      .setMinDF(5).fit(df)
    //将根据语料库(所有文档)中的词频排序从高到低进行选择

    model.vocabulary.foreach(print)

    val tong = model.transform(df)
      tong.withColumn("count" ,tong(colName = "words"))
      .show(false)





  }

}
