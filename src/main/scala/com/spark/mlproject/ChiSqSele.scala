package com.spark.mlproject

import org.apache.spark.ml.feature.ChiSqSelector
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
/**特征选择_卡方选择器
  * @author LTong
  * @date 2019-05-16 14:37
  */
/**
  * +---+------------------+-----+
  * | id|          features|label|
  * +---+------------------+-----+
  * |  1|[0.0,0.0,30.0,1.0]|    1|
  * |  2|[0.0,1.0,20.0,0.0]|    0|
  * |  3|[1.0,0.0,15.0,2.0]|    0|
  * |  4|[0.0,1.0,28.0,0.0]|    1|
  * |  5|[1.0,0.0,27.0,0.0]|    0|
  * +---+------------------+-----+
  *
  * +---+------------------+-----+----------------+
  * |id |features          |label|selectedFeatures|
  * +---+------------------+-----+----------------+
  * |1  |[0.0,0.0,30.0,1.0]|1    |[0.0,30.0]      |
  * |2  |[0.0,1.0,20.0,0.0]|0    |[0.0,20.0]      |
  * |3  |[1.0,0.0,15.0,2.0]|0    |[1.0,15.0]      |
  * |4  |[0.0,1.0,28.0,0.0]|1    |[0.0,28.0]      |
  * |5  |[1.0,0.0,27.0,0.0]|0    |[1.0,27.0]      |
  * +---+------------------+-----+----------------+
  */
object ChiSqSele {
  def main(args: Array[String]): Unit = {
    val spark= SparkSession.builder().master("local").appName("卡方特征选择").getOrCreate()
    import spark.implicits._

    val df = spark.createDataFrame(Seq(
      (1,Vectors.dense(0,0,30,1),1),
      (2,Vectors.dense(0,1,20,0),0),
      (3,Vectors.dense(1,0,15,2),0),
      (4,Vectors.dense(0,1,28,0),1),  //这里第一个0变为1,选2个特征输出时会不同
      (5,Vectors.dense(1,0,27,0),0)
    )).toDF("id" ,"features","label")

    df.show()
    ////setNumTopFeatures(1):设置只选择和标签关联性最强的2个特征
    val select = new ChiSqSelector().setNumTopFeatures(2)
      .setFeaturesCol("features").setLabelCol("label").setOutputCol("selectedFeatures")
    val selector_model=select.fit(df)
    val result=selector_model.transform(df)
    result.show(false)




  }

}
