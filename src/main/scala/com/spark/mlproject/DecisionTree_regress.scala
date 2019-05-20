package com.spark.mlproject

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.evaluation.RegressionEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.ml.regression.{DecisionTreeRegressionModel, DecisionTreeRegressor}
/**
  * @author LTong
  * @date 2019-05-16 14:15
  */
object DecisionTree_regress {
  case class data_scheam(features:Vector,label:String)
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").getOrCreate()
    import spark.implicits._
    val data=spark.sparkContext.textFile("file:///home/soyo/桌面/spark编程测试数据/soyo2.txt")
      .map(_.split(","))
      .map(x=>data_scheam(Vectors.dense(x(0).toDouble,x(1).toDouble,x(2).toDouble,x(3).toDouble),x(4))).toDF()


    val labelIndexer=new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(data)

    val featuresIndexer=new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4).fit(data)

    val labelCoverter=new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(labelIndexer.labels)
    val Array(trainData,testData)=data.randomSplit(Array(0.7,0.3))
    //决策树回归模型构造设置
    val dtRegressor=new DecisionTreeRegressor().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures")
    //构造机器学习工作流
    val pipelineRegressor=new Pipeline().setStages(Array(labelIndexer,featuresIndexer,dtRegressor,labelCoverter))
    //训练决策树回归模型
    val modelRegressor=pipelineRegressor.fit(trainData)
    //进行预测
    val prediction=modelRegressor.transform(testData)
    prediction.show(150)
    //评估决策树回归模型
    val evaluatorRegressor=new RegressionEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("rmse") //setMetricName:设置决定你的度量标准是均方根误差还是均方误差等,值可以为：rmse,mse,r2,mae
    val Root_Mean_Squared_Error=evaluatorRegressor.evaluate(prediction)
    println("均方根误差为: "+Root_Mean_Squared_Error)

    val treeModelRegressor=modelRegressor.stages(2).asInstanceOf[DecisionTreeRegressionModel]
    val schema_decisionTree=treeModelRegressor.toDebugString
    println("决策树分类模型的结构为: "+schema_decisionTree)
  }

}
