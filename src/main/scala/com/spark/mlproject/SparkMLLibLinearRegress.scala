package com.spark.mlproject

import org.apache.spark.SparkConf
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.regression.{LinearRegression, LinearRegressionModel}
import org.apache.spark.sql.SparkSession

object SparkMLLibLinearRegress {
    def main(args: Array[String]): Unit = {
        val conf = new SparkConf()
        conf.setAppName("ml_linearRegress")
        conf.setMaster("local[*]")

        val spark = SparkSession.builder().config(conf).getOrCreate()
        //1.定义样例类
        case class Wine(FixedAcidity: Double, VolatileAcidity: Double, CitricAcid: Double,
                        ResidualSugar: Double, Chlorides: Double, FreeSulfurDioxide: Double,
                        TotalSulfurDioxide: Double, Density: Double, PH: Double,
                        Sulphates: Double, Alcohol: Double,
                        Quality: Double)

        //2.加载csv红酒文件，变换形成rdd
        val file = "file:///D:\\mr\\spark\\red.csv";
        val wineDataRDD = spark.sparkContext.textFile(file)
            .map(_.split(";"))
            .map(w => Wine(w(0).toDouble, w(1).toDouble, w(2).toDouble, w(3).toDouble,
                w(4).toDouble, w(5).toDouble, w(6).toDouble, w(7).toDouble, w(8).toDouble,
                w(9).toDouble, w(10).toDouble,
                w(11).toDouble))

        //导入sparksession的隐式转换对象的所有成员，才能将rdd转换成Dataframe   toDF才能生效
        import spark.implicits._
        val trainingDF = wineDataRDD.map(w => (w.Quality, Vectors.dense(w.FixedAcidity, w.VolatileAcidity,
            w.CitricAcid, w.ResidualSugar, w.Chlorides, w.FreeSulfurDioxide, w.TotalSulfurDioxide,
            w.Density, w.PH, w.Sulphates, w.Alcohol))).toDF("label", "features")
        trainingDF.show(100, false)

        //3.创建线性回归对象
        val lr = new LinearRegression()

        //4.设置回归对象参数
        lr.setMaxIter(2)

        //5.拟合模型
        val model = lr.fit(trainingDF)
        //持久化线性函数
        model.save("file:///d:/java/ml/lrmodel");
        //从外加载线性函数
        LinearRegressionModel.load("file:///d:/java/ml/lrmodel")
        //6.构造测试数据集
        val testDF = spark.createDataFrame(Seq((5.0, Vectors.dense(7.4, 0.7, 0.0, 1.9, 0.076, 25.0, 67.0, 0.9968, 3.2, 0.68, 9.8)),
            (5.0, Vectors.dense(7.8, 0.88, 0.0, 2.6, 0.098, 11.0, 34.0, 0.9978, 3.51, 0.56, 9.4)),
            (7.0, Vectors.dense(7.3, 0.65, 0.0, 1.2, 0.065, 15.0, 18.0, 0.9968, 3.36, 0.57, 9.5))))
            .toDF("label", "features")

        //7.对测试数据集注册临时表
        testDF.createOrReplaceTempView("test")

        //8.使用训练的模型对测试数据进行预测,并提取查看项
        val tested = model.transform(testDF)
        tested.show(100, false)

        //
        val tested2 = tested.select("features", "label", "prediction")
        tested2.show(100, false)

        //9.展示预测结果
        tested.show()

        //10.通过测试数据集只抽取features，作为预测数据。
        val predictDF = spark.sql("select features from test")
        predictDF.show(100, false)
        model.transform(predictDF).show(1000, false)
    }

}
