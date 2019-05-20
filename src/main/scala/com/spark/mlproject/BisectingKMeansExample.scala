package com.spark.mlproject

/**
  * @author LTong
  * @date 2019-05-16 17:53
  */
import org.apache.spark.ml.clustering.BisectingKMeans
import org.apache.spark.sql.SparkSession

/**
  * Bisecting k-means（二分k均值算法）
  */

object BisectingKMeansExample {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("BisectingKMeansExample")
      .getOrCreate()
    val dataset = spark.read.format("libsvm").load("data/mllib/sample_kmeans_data.txt")

    val bkm = new BisectingKMeans()
      //表示期望的聚类的个数
      .setK(2)
      //表示方法单次运行时最大的迭代次数
      .setMaxIter(100)
      //集群初始化时的种子
      .setSeed(1)
    val model = bkm.fit(dataset)

    // 评估聚类结果误差平方和（Sum of the Squared Error，简称SSE）.
    val cost = model.computeCost(dataset)
    println(s"Within Set Sum of Squared Errors = $cost")


    println("Cluster Centers: ")
    val centers = model.clusterCenters
    centers.foreach(println)

    spark.stop()
  }
}
