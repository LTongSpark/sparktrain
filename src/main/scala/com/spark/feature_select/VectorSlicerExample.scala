
package com.spark.feature_select
import java.util.Arrays
import org.apache.spark.ml.attribute.{Attribute, AttributeGroup, NumericAttribute}
import org.apache.spark.ml.feature.VectorSlicer
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.StructType
// $example off$
import org.apache.spark.sql.SparkSession

/**
  * 向量切片机
  *
  * 是一个转换器,采用特征向量，并输出一个新的特征向量与原始特征的子阵列，从向量列中提取特征很有用
  * 接受具有指定索引的向量列，然后输出一个新的向量列，其值通过这些索引进行选择，有两种类型的指数：
  * 代表向量中的索引的整数索引，setIndices()
  *
  * 表示向量中特征名称的字符串索引，setName()此类要求向量列有AttributeGroup，因为实现Attribute的name字段上匹配
  *
  * 整数和字符串的规格课接受，此外。可以同时使用整数索引和字符串名称，必须至少选择一个特征，重复的功能不允许的，
  * 所以选择的索引和名称之间不能重复，请注意，如果选择了功能的索引，则会遇到空的输入会报出异常
  *
  */
object VectorSlicerExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("VectorSlicerExample")
      .getOrCreate()

    val data = Arrays.asList(
      Row(Vectors.sparse(3, Seq((0, -2.0), (1, 2.3)))),
      Row(Vectors.dense(-2.0, 2.3, 0.0))
    )

    val defaultAttr = NumericAttribute.defaultAttr
    val attrs = Array("f1", "f2", "f3").map(defaultAttr.withName)
    val attrGroup = new AttributeGroup("userFeatures", attrs.asInstanceOf[Array[Attribute]])

    val dataset = spark.createDataFrame(data, StructType(Array(attrGroup.toStructField())))

    val slicer = new VectorSlicer().setInputCol("userFeatures").setOutputCol("features")

    slicer.setIndices(Array(1)).setNames(Array("f3"))

    val output = slicer.transform(dataset)
    output.show(false)
  }
}
