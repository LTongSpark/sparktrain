package com.spark.mlproject

import com.spark.mlproject.Gauss_mixture.GMM_Schema
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession

/**
  * @author LTong
  * @date 2019-05-16 14:10
  */
object KMeans {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    import spark.implicits._
    val data = spark.sparkContext.textFile("file:///D:/mr/soyo2.txt")
      .map(_.split(","))
      .map(x=>GMM_Schema(Vectors.dense(x(0).toDouble,x(1).toDouble,x(2).toDouble,x(3).toDouble))).toDF()

    val KMeansModel=new KMeans().setK(3).setFeaturesCol("features").setPredictionCol("prediction").fit(data)
    val results=KMeansModel.transform(data)
    results.show(150)
    //模型所有的聚类中心(指最后生成的聚类中心,K是几就有几组)的情况
    KMeansModel.clusterCenters.foreach(println)
    //集合内误差平方和(选取K的大小可以参照,使用场景+最大的集合内误差平方的值=较合适的K)
    val cost=KMeansModel.computeCost(data)
    println(cost)
  }

}
