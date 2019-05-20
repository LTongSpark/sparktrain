package com.spark.mlproject
import org.apache.spark.ml.feature.VectorIndexer
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.SparkSession
/**
  *
  * 倘若所有特征都已经被组织在一个向量中，又想对其中某些单个分量进行处理时，Spark ML提供了VectorIndexer类来解决向量数据集中的类别性特征转换。
  *
  * 通过为其提供maxCategories超参数，它可以自动识别哪些特征是类别型的，并且将原始值转换为类别索引。它基于不同特征值的数量来识别哪些特征需要被类别化，
  * 那些取值可能性最多不超过maxCategories的特征需要会被认为是类别型的。
  *
  *
  *特征变化--->特征向量中部分特征到类别索引的转换（VectorIndexer）
  * @author LTong
  * @date 2019-05-16 14:42
  */
object VectorIndexerdemo {
  def main(args: Array[String]): Unit = {
    val spark=SparkSession.builder().master("local[2]").appName("IndexToString").getOrCreate()
    import spark.implicits._
    val data=Seq(
      Vectors.dense(-1,1,1,8,56),
      Vectors.dense(-1,3,-1,-9,88),
      Vectors.dense(0,5,1,10,96),
      Vectors.dense(0,5,1,11,589),
      Vectors.dense(0,5,1,11,688)

    )
    val df=spark.createDataFrame(data.map(Tuple1.apply)).toDF("features")
    df.show(10,false)
    //那些取值可能性最多不超过maxCategories的特征会被认为是类别型的,进而将原始值转换为类别索引
    val indexer= new VectorIndexer().setInputCol("features").setOutputCol("indexed").setMaxCategories(3)
    val indexer_model=indexer.fit(df)
    val categoricalFeatures= indexer_model.categoryMaps.keys.toSet
    println(s"Chose ${categoricalFeatures.size} categorical features: " + categoricalFeatures.mkString(", "))
    val indexed=indexer_model.transform(df)
    indexed.show(false)

  }

}
