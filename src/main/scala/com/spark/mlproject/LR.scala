package com.spark.mlproject

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.{HashingTF, Tokenizer}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.linalg.Vector

/**
  * @author LTong
  * @date 2019-05-16 15:34
  */
object LR {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local[2]").appName("逻辑回归").getOrCreate()
    import spark.implicits._
      val training = spark.createDataFrame(Seq((0,"soyo spark soyo1",1.0),(1,"hadoop spark",1.0),(2,"zhouhang xiaohai",0.0),(3,"hbase spark hive soyo",1.0))).
        toDF("id","text","label")

    //转换器

    val tokenizer = new Tokenizer().setInputCol("text").setOutputCol("words")
    val hashingTF=new HashingTF().setNumFeatures(1000).setInputCol(tokenizer.getOutputCol).setOutputCol("features")
    //评估器
    val lr= new LogisticRegression().setMaxIter(10). //设置最大迭代次数
      setRegParam(0.01) // 设置正则化参数
    val pipeline= new Pipeline().setStages(Array(tokenizer,hashingTF,lr))


    //训练出的模型
    val model=pipeline.fit(training)
    //测试数据
    val test= spark.createDataFrame(Seq((4,"spark i like"),(5,"hadoop spark book"),(6,"soyo9 soy 88"))).toDF("id","text")
    model.transform(test).show(false)
    model.transform(test)
      .select("id","text","probability","prediction")
      .collect()
      .foreach { case Row(id: Int, text: String, prob: Vector, prediction: Double) =>
        println(s"($id,$text)----->prob=$prob,prediction=$prediction")
      }
    //转换器生成的一些中间数据
    model.transform(test).select("id","text","features","rawPrediction")
      .collect()
      .foreach{
        case Row(id:Int,text:String,features:Vector,rawPrediction:Vector)=>
          println(s"id=$id,text=$text,features=$features,rawPrediction=$rawPrediction")
      }

    spark.stop()




  }

}
