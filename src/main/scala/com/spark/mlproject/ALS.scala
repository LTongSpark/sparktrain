package com.spark.mlproject

import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.recommendation.ALS
import org.apache.spark.sql.SparkSession

/**
  * 协同过滤算法
  *
  * @author LTong
  * @date 2019-05-16 10:50
  */

/**
  *
  * numBlocks 是用于并行化计算的用户和商品的分块个数 (默认为10)。
  * rank 是模型中隐语义因子的个数（默认为10）。
  *  maxIter 是迭代的次数（默认为10）。
  *  regParam 是ALS的正则化参数（默认为1.0）。
  *  implicitPrefs 决定了是用显性反馈ALS的版本还是用适用隐性反馈数据集的版本（默认是false，即用显性反馈）。
  *  alpha 是一个针对于隐性反馈 ALS 版本的参数，这个参数决定了偏好行为强度的基准（默认为1.0）。
  *  nonnegative 决定是否对最小二乘法使用非负的限制（默认为false）。

  */
object ALS {
  case class schema_data(userId:Int ,movieId:Int ,score:Float ,timestamp:Long)
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .master("local[*]")
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    val data = spark.sparkContext.textFile("file:///D:\\mr\\sample_movielens_ratings.txt")

    import spark.implicits._
    val dataDF = data.map(_.split("::")).map(rdd =>schema_data(rdd(0).toInt,rdd(1).toInt,rdd(2).toFloat,rdd(3).toLong)).toDF()

    //创建训练数据和测试数据

    val Array(trainData,testData) = dataDF.randomSplit(Array(0.8,0.2))

    //als建立推荐模型，显性反馈  代表偏好程度

    val als_Explicit = new ALS().setMaxIter(5).setRegParam(0.02).setImplicitPrefs(false).setNonnegative(true)
      .setUserCol("userId")
      .setItemCol("movieId")
      .setRatingCol("score")
    //ALS建立推荐模型,隐性反馈--->隐性反馈数值代表置信度,隐性反馈的数值通常是动作的频次,频次越多,并不代表偏好值越大.
    val als_Implicit=new ALS().setMaxIter(5).setRegParam(0.01).setImplicitPrefs(true).setNonnegative(true)
      .setUserCol("userId")
      .setItemCol("movieId")
      .setRatingCol("score")

    //训练模型(显性)
    val modelExplicit=als_Explicit.fit(trainData)
    //冷启动处理：Spark允许用户将coldStartStrategy参数设置为“drop”,以便在包含NaN值的预测的DataFrame中删除任何行
    modelExplicit.setColdStartStrategy("drop")

    //训练模型(隐性)
    val modelImplicit=als_Implicit.fit(trainData)
    //冷启动处理：Spark允许用户将coldStartStrategy参数设置为“drop”,以便在包含NaN值的预测的DataFrame中删除任何行
    modelImplicit.setColdStartStrategy("drop")



    //测试数据
    val predictionExplicit=modelExplicit.transform(testData)
    val predictionImplicit=modelImplicit.transform(testData)

    predictionExplicit.show(10)
    predictionImplicit.show(10)

    //模型评估
    val evaluator = new RegressionEvaluator().setMetricName("rmse")
      .setLabelCol("score")
      .setPredictionCol("prediction")
    val rmseExplicit=evaluator.evaluate(predictionExplicit)
    val rmseImplicit=evaluator.evaluate(predictionImplicit)
    println("显性反馈-->均方根误差为:"+rmseExplicit)
    println("隐性反馈-->均方根误差为:"+rmseImplicit)

    //为每个用户提供10大电影排名
    val movie= modelExplicit.recommendForAllUsers(10)
    //为每个电影推荐10个候选人
    val movieUser = modelExplicit.recommendForAllItems(10)

    movie.show(10,false)
    movieUser.show(10 ,false)






  }

}
