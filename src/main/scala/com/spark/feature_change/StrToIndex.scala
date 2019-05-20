package com.spark.feature_change

import org.apache.spark.ml.feature.StringIndexer
import org.apache.spark.sql.SparkSession

/**
  * 特征变化--->标签到索引的转换（StringIndexer）
  *StringIndexer 将一列字符串标签编码成一列下标标签，下标范围在[0, 标签数量)，顺序是标签的出现频率。
  * 所以最经常出现的标签获得的下标就是0。如果输入列是数字的，我们会将其转换成字符串，然后将字符串改为下标。
  * 当下游管道组成部分，比如说Estimator 或Transformer 使用将字符串转换成下标的标签时，
  * 你必须将组成部分的输入列设置为这个将字符串转换成下标后的列名。很多情况下，你可以使用setInputCol设置输入列。
  * @author LTong
  * @date 2019-05-16 15:04
  */
object StrToIndex {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()

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
