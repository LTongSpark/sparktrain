package com.spark.feature_change

import org.apache.spark.ml.feature.{IndexToString, StringIndexer}
import org.apache.spark.sql.SparkSession

/**特征变化--->索引到标签的转换（IndexToString）
  *IndexToString的作用是把标签索引的一列重新映射回原有的字符型标签。一般都是和StringIndexer配合，先用StringIndexer转化成标签索引，
  * 进行模型训练，然后在预测标签的时候再把标签索引转化成原有的字符标签。
  * @author LTong
  * @date 2019-05-16 14:57
  */
object IndexToStr {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("IndexToString").getOrCreate()

    val df=spark.createDataFrame(Seq(
      (0,"log"),
      (1,"text"),
      (2,"text"),
      (3,"soyo"),
      (4,"text"),
      (5,"log"),
      (6,"log"),
      (7,"log")
    )).toDF("id","label")
    val model=new StringIndexer().setInputCol("label").setOutputCol("label_index").fit(df)
    val indexed=model.transform(df)
    indexed.show()
//    indexed.createOrReplaceTempView("soyo")
//    spark.sql("select * from soyo ").show()
//    spark.sql("select distinct label,label_index from soyo ").show()  //去重
    //把标签索引的一列重新映射回原有的字符型标签
    val converter=new IndexToString().setInputCol("label_index").setOutputCol("original_index")
    val converted=converter.transform(indexed)
    converted.show()
  }

}
