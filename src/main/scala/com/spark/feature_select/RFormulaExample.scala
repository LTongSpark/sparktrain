
package com.spark.feature_select

import org.apache.spark.ml.feature.RFormula
import org.apache.spark.sql.SparkSession

/**
  * r模型公式
  * 产生一个特征向量和一个标签的double列或label累，像r在线性回归中使用公式时，
  * 字符型的输入将转换为onehot编码，数字列被转换为双精度，如果label列是类型字符串
  * 则它将首先使用stringIndexer转换为double，如果dataframe中不存在label列，则会从公式中响应变量创建输出标签列
  */
object RFormulaExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("RFormulaExample")
      .getOrCreate()

    val dataset = spark.createDataFrame(Seq(
      (7, "US", 18, 1.0),
      (8, "CA", 12, 0.0),
      (9, "NZ", 15, 0.0)
    )).toDF("id", "country", "hour", "clicked")

    val formula = new RFormula()
      .setFormula("clicked ~ country + hour")
      .setFeaturesCol("features")
      .setLabelCol("label")

    val output = formula.fit(dataset).transform(dataset)
    output.show()
  }
}
