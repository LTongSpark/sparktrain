package com.spark.mlproject

import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.sql.SparkSession

/**
  * @author LTong
  * @date 2019-05-15 16:51
  */
object SVM {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName(s"{${this.getClass.getSimpleName}")
      .master("local[*]").getOrCreate()

    //训练数据集
    val train_data = spark.sparkContext.textFile("")
    //测试数据集
    val text_data = spark.sparkContext.textFile("")

    val train = train_data.map(x =>{
      val part = x.split(",")
      LabeledPoint(part(4) .toDouble ,Vectors.dense(part(0).toDouble,part(1).toDouble ,part(2).toDouble ,part(3).toDouble))
    }).cache()
    val text = text_data.map(x =>{
      val part = x.split(",")
      LabeledPoint(part(4) .toDouble ,Vectors.dense(part(0).toDouble,part(1).toDouble ,part(2).toDouble ,part(3).toDouble))
    })

    //设置迭代次数为100
    SVMWithSGD.train(train ,100)



  }

}
