package com.spark.mlproject

import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.feature.{IndexToString, StringIndexer}
/**特征变化--->索引到标签的转换（IndexToString）
  *
  * @author LTong
  * @date 2019-05-16 14:57
  */
object IndexToStr {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("IndexToString").getOrCreate()
    import spark.implicits._

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
