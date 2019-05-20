package com.spark.mlproject

import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.sql.SparkSession

/**
  * @author LTong
  * @date 2019-05-16 14:47
  */
object OneHot {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    val df=spark.createDataFrame(Seq(
      (0,"log"),
      (1,"text"),
      (1,"text"),
      (3,"soyo"),
      (4,"text"),
      (5,"log"),
      (6,"log"),
      (7,"log"),
      (8,"hadoop")
    )).toDF("id","label")

    val df2=spark.createDataFrame(Seq(
      (0,"log"),
      (1,"soyo"),
      (2,"soyo")
    )).toDF("id","label")

    val indexer=new StringIndexer().setInputCol("label").setOutputCol("label_index")

    val model=indexer.fit(df)
    val indexed1=model.transform(df)//这里测试数据用的是df
    indexed1.show()
    val indexed=model.transform(df2)//测试数据换为df2
    val encoder=new OneHotEncoder().setInputCol("label_index").setOutputCol("lable_vector").setDropLast(false)  //setDropLast：被编码为全0向量的标签也可以占有一个二进制特征
    val encodered1=encoder.transform(indexed1)
    encodered1.show()
    val encodered=encoder.transform(indexed)//(4,[2],[1.0]) //这里的4表示训练数据中有4中类型的标签
    encodered.show()




  }

}
