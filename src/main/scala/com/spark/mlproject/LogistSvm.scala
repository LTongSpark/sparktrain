package com.spark.mlproject

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession

/**
  * 交叉验证_自动获取模型最优超参数
  * @author LTong
  * @date 2019-05-15 17:23
  */
object LogistSvm {
  case class Wine(FixedAcidity: Double, VolatileAcidity: Double,
                  CitricAcid: Double, ResidualSugar: Double, Chlorides: Double,
                  FreeSulfurDioxide: Double, TotalSulfurDioxide: Double, Density: Double,
                  PH: Double, Sulphates: Double, Alcohol: Double, Quality: Double)
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName(s"${this.getClass.getSimpleName}").master("local[*]").getOrCreate()

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

    //我的label本来就是double类型 不用转成数字
    //StringIndexer对数据集的label进行重新编号就很容易理解了，都是采用类似的转换思路，看下面的例子就可以了
//    val labelIndexer=new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(trainingDF)
//    labelIndexer.labels.foreach(print)
//    //对数据集的特征向量中的类别特征进行标号
    val featuresIndexer=new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").fit(trainingDF)
   // print(featuresIndexer)
    //分割数据进行计算
    val Array(trainData,testData)=trainingDF.randomSplit(Array(0.7,0.3))


    val lr = new LogisticRegression().setLabelCol("label").setFeaturesCol("indexedFeatures")
      .setMaxIter(10)


    //val labelConverter=new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)
    //机器学习工作流
    val lrPipeline=new Pipeline().setStages(Array(featuresIndexer,lr))
    val lrdata = lrPipeline.fit(trainData)
    //交叉验证需要的模型评估
    val evaluator=new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction")

    //构造参数网格
    val paramGrid=new ParamGridBuilder().addGrid(lr.regParam,Array(0.01,0.3,0.8)).addGrid(lr.elasticNetParam,Array(0.3,0.9)).build()
    //构建机器学习工作流的交叉验证,定义验证模型,模型评估,参数网格,数据集的折叠数(交叉验证原理)
    val cv=new CrossValidator().setEstimator(lrPipeline).setEvaluator(evaluator).setEstimatorParamMaps(paramGrid).setNumFolds(8)

    //训练模型
    val cvModel=cv.fit(trainData)

    //测试数据
    val lrPrediction=cvModel.transform(testData)
    lrPrediction.show(30 ,false)

    val evaluator2=new MulticlassClassificationEvaluator().setLabelCol("label").setPredictionCol("prediction")
    val lrAccuracy=evaluator2.evaluate(lrPrediction)
    println("准确率为: "+lrAccuracy)
    println("错误率为： "+(1-lrAccuracy))
    //获取最优模型
    val bestModel=cvModel.bestModel.asInstanceOf[PipelineModel]

    val lrModel=bestModel.stages(2).asInstanceOf[LogisticRegressionModel]
    println("二项逻辑回归模型系数矩阵: "+lrModel.coefficientMatrix)
    println("二项逻辑回归模型的截距向量: "+lrModel.interceptVector)
    println("类的数量(标签可以使用的值): "+lrModel.numClasses)
    println("模型所接受的特征的数量: "+lrModel.numFeatures)

    println("所有参数的设置为： "+lrModel.explainParams())
    println("最优的regParam的值为: "+lrModel.explainParam(lrModel.regParam))
    println("最优的elasticNetParam的值为: "+lrModel.explainParam(lrModel.elasticNetParam))



  }


}
