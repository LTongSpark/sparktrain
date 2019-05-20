
package com.spark.feature_change


import org.apache.spark.ml.feature.QuantileDiscretizer

import org.apache.spark.sql.SparkSession

/**
  * 分位离散化
  *
  *
  * QuantileDiscretizer输入连续的特征列,输出分箱的类别特征。分箱数是通过参数numBuckets来指定的。
  * 箱的范围是通过使用近似算法来得到的。 近似的精度可以通过relativeError参数来控制。
  * 当这个参数设置为0时,将会计算精确的分位数。箱的上边界和下边界分别是正无穷和负无穷时, 取值将会覆盖所有的实数值。
  */
object QuantileDiscretizerExample {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("QuantileDiscretizerExample")
      .getOrCreate()

    val data = Array((0, 18.0), (1, 19.0), (2, 8.0), (3, 5.0), (4, 2.2))
    val df = spark.createDataFrame(data).toDF("id", "hour")
        .repartition(1)


    val discretizer = new QuantileDiscretizer()
      .setInputCol("hour")
      .setOutputCol("result")
      .setNumBuckets(3)

    val result = discretizer.fit(df).transform(df)
    result.show()

  }
}
