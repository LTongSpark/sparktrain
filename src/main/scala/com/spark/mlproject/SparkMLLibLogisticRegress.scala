package com.spark.mlproject

/**
  * 使用spark的逻辑回归分类白酒质量
  */

import org.apache.spark.SparkConf
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

object SparkMLLibLogisticRegress {


    def main(args: Array[String]): Unit = {

        val conf = new SparkConf()
        conf.setAppName("ml_linearRegress")
        conf.setMaster("local[*]")

        val spark = SparkSession.builder().config(conf).getOrCreate()
        //1.定义样例类
        case class Wine(FixedAcidity: Double, VolatileAcidity: Double,
                        CitricAcid: Double, ResidualSugar: Double, Chlorides: Double,
                        FreeSulfurDioxide: Double, TotalSulfurDioxide: Double, Density: Double,
                        PH: Double, Sulphates: Double, Alcohol: Double, Quality: Double)

        //2.加载csv红酒文件，变换形成rdd
        val file = "file:///D:\\mr\\white.csv";
        val wineDataRDD = spark.sparkContext.textFile(file)
            .map(_.split(";"))
            .map(w => Wine(w(0).toDouble, w(1).toDouble, w(2).toDouble, w(3).toDouble,
                w(4).toDouble, w(5).toDouble, w(6).toDouble, w(7).toDouble, w(8).toDouble,
                w(9).toDouble, w(10).toDouble,
                w(11).toDouble))

        //导入sparksession的隐式转换对象的所有成员，才能将rdd转换成Dataframe
        import spark.implicits._
        val trainingDF = wineDataRDD.map(w => (if (w.Quality < 7) 0D else
            1D, Vectors.dense(w.FixedAcidity, w.VolatileAcidity, w.CitricAcid,
            w.ResidualSugar, w.Chlorides, w.FreeSulfurDioxide, w.TotalSulfurDioxide,
            w.Density, w.PH, w.Sulphates, w.Alcohol))).toDF("label", "features")


        trainingDF.show(4000 ,false)
        //3.创建逻辑回归对象
        val lr = new LogisticRegression()
        lr.setMaxIter(10).setRegParam(0.01)

        //4.拟合训练数据，生成模型
        val model = lr.fit(trainingDF)

        //5.构造测试数据
        val testDF = spark.createDataFrame(Seq(
            (1.0, Vectors.dense(6.1, 0.32, 0.24, 1.5, 0.036, 43, 140, 0.9894, 3.36, 0.64, 10.7)),
            (0.0, Vectors.dense(5.2, 0.44, 0.04, 1.4, 0.036, 38, 124, 0.9898, 3.29, 0.42, 12.4)),
            (0.0, Vectors.dense(7.2, 0.32, 0.47, 5.1, 0.044, 19, 65, 0.9951, 3.38, 0.36, 9)),
            (0.0, Vectors.dense(6.4, 0.595, 0.14, 5.2, 0.058, 15, 97, 0.991, 3.03, 0.41, 12.6)))
        ).toDF("label", "features")

        testDF.createOrReplaceTempView("test")

        //预测测试数据
        val tested = model.transform(testDF).select("features", "label", "prediction")

        //
        val realData = spark.sql("select features from test")

        model.transform(realData).select("features", "prediction").show(100, false)
    }
}

