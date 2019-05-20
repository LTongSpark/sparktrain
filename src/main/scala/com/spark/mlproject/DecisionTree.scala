package com.spark.mlproject

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{DecisionTreeClassificationModel, DecisionTreeClassifier}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.sql.SparkSession


/**+-----------------+------+------------+-----------------+--------------+-------------+----------+--------------+
  * |         features| label|indexedLabel|  indexedFeatures| rawPrediction|  probability|prediction|predictedLabel|
  * +-----------------+------+------------+-----------------+--------------+-------------+----------+--------------+
  * |[4.4,3.0,1.3,0.2]|hadoop|         1.0|[4.4,3.0,1.3,0.2]|[0.0,36.0,0.0]|[0.0,1.0,0.0]|       1.0|        hadoop|
  * |[4.6,3.4,1.4,0.3]|hadoop|         1.0|[4.6,3.4,1.4,0.3]|[0.0,36.0,0.0]|[0.0,1.0,0.0]|       1.0|        hadoop|
  * |[4.6,3.6,1.0,0.2]|hadoop|         1.0|[4.6,3.6,1.0,0.2]|[0.0,36.0,0.0]|[0.0,1.0,0.0]|       1.0|        hadoop|
  * |[4.9,2.4,3.3,1.0]| spark|         0.0|[4.9,2.4,3.3,1.0]| [0.0,0.0,1.0]|[0.0,0.0,1.0]|       2.0|         Scala|
  * |[5.0,2.0,3.5,1.0]| spark|         0.0|[5.0,2.0,3.5,1.0]| [1.0,0.0,0.0]|[1.0,0.0,0.0]|       0.0|         spark|
  * |[5.0,2.3,3.3,1.0]| spark|         0.0|[5.0,2.3,3.3,1.0]|[29.0,0.0,0.0]|[1.0,0.0,0.0]|       0.0|         spark|
  *
  * 准确率为: 0.8958333333333334
  * 错误率为: 0.10416666666666663
  */
/**
  * @author LTong
  * @date 2019-05-16 14:22
  */
object DecisionTree {
  case class data_schemas(features:Vector,label:String)
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").appName("决策树").getOrCreate()
    import spark.implicits._

    val source_DF=spark.sparkContext.textFile("file:///home/soyo/桌面/spark编程测试数据/soyo2.txt")
      .map(_.split(",")).map(x=>data_schemas(Vectors.dense(x(0).toDouble,x(1).toDouble,x(2).toDouble,x(3).toDouble),x(4))).toDF()
    source_DF.createOrReplaceTempView("decisonTree")
    val DF=spark.sql("select * from decisonTree")
    DF.show()
    //分别获取标签列和特征列,进行索引和重命名(索引的目的是将字符串label数值化方便机器学习算法学习)
    val lableIndexer=new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(DF)
    val featureIndexer= new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").setMaxCategories(4).fit(DF)
    val labelConverter= new IndexToString().setInputCol("prediction").setOutputCol("predictedLabel").setLabels(lableIndexer.labels)
    // 训练数据和测试数据
    val Array(trainData,testData)=DF.randomSplit(Array(0.7,0.3))
    val decisionTreeClassifier=new DecisionTreeClassifier().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures")
    //构建机器学习工作流
    val dt_pipeline=new Pipeline().setStages(Array(lableIndexer,featureIndexer,decisionTreeClassifier,labelConverter))
    val dt_model=dt_pipeline.fit(trainData)
    //进行预测
    val dtprediction=dt_model.transform(testData)
    dtprediction.show(150)
    //评估决策树模型
    val evaluatorClassifier=new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction").setMetricName("accuracy")
    val accuracy=evaluatorClassifier.evaluate(dtprediction)
    println("准确率为: "+accuracy)
    val error=1-accuracy
    println("错误率为: "+error)
    val treeModelClassifier=dt_model.stages(2).asInstanceOf[DecisionTreeClassificationModel]
    val schema_DecisionTree=treeModelClassifier.toDebugString
    println("决策树的模型结构为: "+schema_DecisionTree)
  }


}
