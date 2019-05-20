package com.spark.mlproject

import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.SparkSession

/**
  * 特征变化--->标签到索引的转换（StringIndexer）
  *
  * @author LTong
  * @date 2019-05-16 15:04
  */
object StrToIndex {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()

    import  spark.implicits._

    val data = spark.createDataFrame(Seq(
      (0,"log"),
      (1,"text"),
      (2,"text"),
      (3,"soyo"),
      (4,"text"),
      (5,"log"),
      (6,"log"),
      (7,"log")
    )).toDF("label" ,"word")

    val index = new StringIndexer().setInputCol("word").setOutputCol("word_index")
    val model = index.fit(data)
    val indexer  = model.transform(data)
    indexer.show()



  }

}
