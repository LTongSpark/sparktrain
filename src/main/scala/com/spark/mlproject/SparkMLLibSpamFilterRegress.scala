package com.spark.mlproject

/**
  * 使用spark的逻辑回归管线实现垃圾邮件分类
  */

import org.apache.spark.SparkConf
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql.SparkSession

object SparkMLLibSpamFilterRegress {
    def main(args: Array[String]): Unit = {

        val conf = new SparkConf()
        conf.setAppName("ml_linearRegress")
        conf.setMaster("local[*]")

        val spark = SparkSession.builder().config(conf).getOrCreate()

        //1.创建训练数据集
        val training = spark.createDataFrame(Seq(
            ("you@example.com", "hope you are well", 0.0),
            ("raj@example.com", "nice to hear from you", 0.0),
            ("thomas@example.com", "happy holidays", 0.0),
            ("mark@example.com", "see you tomorrow", 0.0),
            ("xyz@example.com", "save money", 1.0),
            ("top10@example.com", "low interest rate", 1.0),
            ("marketing@example.com", "cheap loan", 1.0)))
            .toDF("email", "message", "label")

        //2.创建分词器(压扁行成单词集合)
        val tokenizer = new Tokenizer().setInputCol("message").setOutputCol("words")

        //3.将单词换算成特征向量()
        val hashingTF = new HashingTF().setNumFeatures(1000).setInputCol("words").setOutputCol("features")

        //4.逻辑回归
        val lr = new LogisticRegression().setMaxIter(10).setRegParam(0.01)

        //将2,3,4合成一个链条
        val pipeline = new Pipeline().setStages(Array(tokenizer, hashingTF, lr))

        //通过管线对训练数据进行拟合，生成管线模型。
        val model = pipeline.fit(training)

        //准备测试数据
        val test = spark.createDataFrame(Seq(
            ("you@example.com", "how are you"),
            ("jain@example.com", "hope doing well"),
            ("caren@example.com", "want some money"),
            ("zhou@example.com", "secure loan"),
            ("ted@example.com", "need loan")))
            .toDF("email", "message")

        //对测试数据进行预测
        val prediction = model.transform(test).select("email", "message", "prediction").show(1000, false)
    }
}

