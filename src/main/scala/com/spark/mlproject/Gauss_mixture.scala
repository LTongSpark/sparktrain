package com.spark.mlproject

import org.apache.spark.ml.clustering.GaussianMixture
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.linalg.{Vector, Vectors}
/**
  * @author LTong
  * @date 2019-05-16 13:48
  */
object Gauss_mixture {
  case class GMM_Schema(features:Vector)
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    import spark.implicits._
    val data = spark.sparkContext.textFile("file:///D:/mr/soyo2.txt")
      .map(_.split(","))
      .map(x=>GMM_Schema(Vectors.dense(x(0).toDouble,x(1).toDouble,x(2).toDouble,x(3).toDouble))).toDF()

    val  Array(training ,test) = data.randomSplit(Array(0.75,0.25))

    val GMM = new GaussianMixture().setK(3).setProbabilityCol("Probability").setPredictionCol("prediction")


    val GMM_model = GMM.fit(training)

    //Probaility表示样本属于各个聚簇的概率-->0,1,2三个簇各个的概率,prediction表示对样本的聚簇归属预测

    /**
      * |[4.3,3.0,1.1,0.1]|0         |[0.9999999999999998,1.2432386954308408E-16,5.947784580810509E-17]|
      * |[4.3,3.0,1.1,0.1]|0         |[0.9999999999999998,1.2432386954308408E-16,5.947784580810509E-17]|
      * |[4.4,2.9,1.4,0.2]|1         |[0.08621867839680582,0.9137813216026631,5.3091430472416E-13]     |
      * |[4.4,2.9,1.4,0.2]|1         |[0.08621867839680582,0.9137813216026631,5.3091430472416E-13]     |
      * |[4.4,2.9,1.4,0.2]|1         |[0.08621867839680582,0.9137813216026631,5.3091430472416E-13]     |
      * |[4.4,2.9,1.4,0.2]|1         |[0.08621867839680582,0.9137813216026631,5.3091430472416E-13]     |
      */
    val result=GMM_model.transform(test)
    result.show(false)

    //GMM不直接给出聚类中心,而是给出各个混合成分（多元高斯分布）的参数
    //weights成员获取到各个混合成分的权重,使用gaussians成员来获取到各个混合成分的参数（均值向量和协方差矩阵）
    for (i<-0 until(GMM_model.getK)){
      println("Component %d : weight is %f\n mu vector is %s\n sigma matrix is %s".format(i,GMM_model.weights(i),GMM_model.gaussians(i).mean,GMM_model.gaussians(i).cov))
    }



  }

}
