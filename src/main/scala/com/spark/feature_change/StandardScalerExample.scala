package com.spark.feature_change

import org.apache.spark.ml.feature.StandardScaler
import org.apache.spark.sql.SparkSession

/**
  * 对于同一个特征，不同的样本中的取值可能会相差非常大，一些异常小或异常大的数据会误导模型的正确训练；另外，
  * 如果数据的分布很分散也会影响训练结果。以上两种方式都体现在方差会非常大。此时，我们可以将特征中的值进行标准差标准化，
  * 即转换为均值为0，方差为1的正态分布。如果特征非常稀疏，并且有大量的0（现实应用中很多特征都具有这个特点），
  * Z-score 标准化的过程几乎就是一个除0的过程，结果不可预料。所以在训练模型之前，一定要对特征的数据分布进行探索，
  * 并考虑是否有必要将数据进行标准化。基于特征值的均值（mean）和标准差（standard deviation）进行数据的标准化。
  * 它的计算公式为：标准化数据=(原数据-均值)/标准差。标准化后的变量值围绕0上下波动，大于0说明高于平均水平，小于0说明低于平均水平。
  *
  * 因为在原始的资料中，各变数的范围大不相同。对于某些机器学习的算法，若没有做过标准化，目标函数会无法适当的运作。
  * 举例来说，多数的分类器利用两点间的距离计算两点的差异， 若其中一个特征具有非常广的范围，那两点间的差异就会被该特征左右，
  * 因此，所有的特征都该被标准化，这样才能大略的使各特征依比例影响距离。另外一个做特征缩放的理由是他能使加速梯度下降法的收敛。

  *
  */
object StandardScalerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("StandardScalerExample")
      .getOrCreate()

    val dataFrame = spark.read.format("libsvm").load("file:///D:/mr/spark-2.2.0-bin-hadoop2.7/data/mllib/sample_libsvm_data.txt")

    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      //将数据缩放到单位标准差
      .setWithStd(true)
      //将数据中心为平均值
      .setWithMean(false)

    val scalerModel = scaler.fit(dataFrame)

    val scaledData = scalerModel.transform(dataFrame)
    scaledData.show()

    spark.stop()
  }
}
