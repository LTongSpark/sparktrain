package com.spark.mlproject
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.{BinaryLogisticRegressionSummary, LogisticRegression, LogisticRegressionModel}
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.sql.SparkSession
/**
  *多项式逻辑回归__多分类
  *
  * @author LTong
  * @date 2019-05-16 14:28
  */


/**
  *
  * +-----------------+-----+------------+-----------------+--------------------+--------------------+----------+---------------+
  * |         features|label|indexedLabel|  indexedFeatures|       rawPrediction|         probability|prediction|predictionLabel|
  * +-----------------+-----+------------+-----------------+--------------------+--------------------+----------+---------------+
  * |[4.4,3.2,1.3,0.2]|soyo1|         1.0|[4.4,3.2,1.3,0.2]|[0.06313829278191...|[0.23858281707128...|       1.0|          soyo1|
  * |[4.6,3.4,1.4,0.3]|soyo1|         1.0|[4.6,3.4,1.4,0.3]|[0.06313829278191...|[0.23750012598226...|       1.0|          soyo1|
  * |[4.7,3.2,1.6,0.2]|soyo1|         1.0|[4.7,3.2,1.6,0.2]|[0.06313829278191...|[0.24710416166321...|       1.0|          soyo1|
  * |[4.8,3.4,1.6,0.2]|soyo1|         1.0|[4.8,3.4,1.6,0.2]|[0.06313829278191...|[0.23716995683018...|       1.0|          soyo1|
  * |[4.8,3.4,1.9,0.2]|soyo1|         1.0|[4.8,3.4,1.9,0.2]|[0.06313829278191...|[0.24567798276462...|       1.0|          soyo1|
  * |[4.9,2.4,3.3,1.0]|soyo2|         0.0|[4.9,2.4,3.3,1.0]|[0.06313829278191...|[0.38071131817453...|       0.0|          soyo2|
  * |[5.0,3.2,1.2,0.2]|soyo1|         1.0|[5.0,3.2,1.2,0.2]|[0.06313829278191...|[0.23576075216827...|       1.0|          soyo1|
  *
  *
  * 准确率为： 0.36458333333333337
  * 错误率为: 0.6354166666666666
  * 二项逻辑回归模型系数矩阵: 3 x 4 CSCMatrix
  * (1,1) 0.35559564188466614
  * (1,2) -0.203185158868005
  * (1,3) -0.43876460704959996
  * (2,3) 0.0283914830858408
  * 二项逻辑回归模型的截距向量: [0.06313829278191783,0.1708622138778958,-0.23400050665981365]
  * 类的数量(标签可以使用的值): 3
  * 模型所接受的特征的数量: 4
  * false
  *
  */
object multinomial_LR {
  case class data_schemas(features:Vector,label:String)
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local").getOrCreate()
    import spark.implicits._  //支持把一个RDD隐式转换为一个DataFrame

    val df =spark.sparkContext.textFile("file:///home/soyo/桌面/spark编程测试数据/soyo.txt")
      .map(_.split(",")).map(x=>data_schemas(Vectors.dense(x(0).toDouble,x(1).toDouble,x(2).toDouble,x(3).toDouble),x(4))).toDF()
    // df.show(150)
    val labelIndexer=new StringIndexer().setInputCol("label").setOutputCol("indexedLabel").fit(df)
    val featureIndexer=new VectorIndexer().setInputCol("features").setOutputCol("indexedFeatures").fit(df)  //目的在特征向量中建类别索引
    val Array(trainData,testData)=df.randomSplit(Array(0.7,0.3))
    val lr=new LogisticRegression().setLabelCol("indexedLabel").setFeaturesCol("indexedFeatures").setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8).setFamily("multinomial")//设置elasticnet混合参数为0.8,setFamily("multinomial"):设置为多项逻辑回归,不设置setFamily为二项逻辑回归
    val labelConverter=new IndexToString().setInputCol("prediction").setOutputCol("predictionLabel").setLabels(labelIndexer.labels)

    val lrPipeline=new Pipeline().setStages(Array(labelIndexer,featureIndexer,lr,labelConverter))
    val lrPipeline_Model=lrPipeline.fit(trainData)
    val lrPrediction=lrPipeline_Model.transform(testData)
    lrPrediction.show(150)


    //模型评估
    val evaluator=new MulticlassClassificationEvaluator().setLabelCol("indexedLabel").setPredictionCol("prediction")
    val lrAccuracy=evaluator.evaluate(lrPrediction)
    println("准确率为： "+lrAccuracy)
    val lrError=1-lrAccuracy
    println("错误率为: "+lrError)
    val LRmodel=lrPipeline_Model.stages(2).asInstanceOf[LogisticRegressionModel]
    println("多项逻辑回归模型系数矩阵: "+LRmodel.coefficientMatrix)
    println("多项逻辑回归模型的截距向量: "+LRmodel.interceptVector)
    println("类的数量(标签可以使用的值): "+LRmodel.numClasses)
    println("模型所接受的特征的数量: "+LRmodel.numFeatures)
    //多项式逻辑回归不包含对模型的摘要总结
    println(LRmodel.hasSummary)




  }

}
