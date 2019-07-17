package com.spark.feature_change

import org.apache.spark.ml.feature.{OneHotEncoder, StringIndexer}
import org.apache.spark.sql.SparkSession

/**
  * 将一列标签随意映射到一列二进制向量，最多只有一个单位，该编码允许期望连续特征的算法使用分类特征
  *
  * 使用onehot编码的好处：
  *
  * 1、将离散特征的取值扩展到了欧式空间，离散特征的某个取值就对应欧式空间的某个点
  *
  * 2、将离散特征通过one-hot编码映射到欧空间，是因为在回归，分类，聚类等机器学习算法中，特征之间距离的计算或者相似
  * 度的计算是非常重要的，而我们常用的距离或者相似度的计算都是建立在欧式空间的相似度计算，计算余弦相似性，基于的就是欧式空间
  *
  * @author LTong
  * @date 2019-05-16 14:47
  */
object OneHot {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName(s"${this.getClass.getSimpleName}")
      .master("local[*]")
      .getOrCreate()
    val df = spark.createDataFrame(Seq(
      (0, "log"),
      (1, "text"),
      (1, "text"),
      (3, "soyo"),
      (4, "text"),
      (5, "log"),
      (6, "log"),
      (7, "log"),
      (8, "hadoop")
    )).toDF("id", "label")

    val df2 = spark.createDataFrame(Seq(
      (0, "log"),
      (1, "soyo"),
      (2, "soyo")
    )).toDF("id", "label")

    val indexer = new StringIndexer().setInputCol("label").setOutputCol("label_index")

    val model = indexer.fit(df)
    val indexed1 = model.transform(df) //这里测试数据用的是df
    indexed1.show()
    val indexed = model.transform(df2)
    //测试数据换为df2
    val encoder = new OneHotEncoder().setInputCol("label_index").setOutputCol("lable_vector").setDropLast(false) //setDropLast：被编码为全0向量的标签也可以占有一个二进制特征
    val encodered1 = encoder.transform(indexed1)
    encodered1.show()
    val encodered = encoder.transform(indexed) //(4,[2],[1.0]) //这里的4表示训练数据中有4中类型的标签
    encodered.show()
  }

}
