
package com.spark.feature_change

/**
  *
  *
  * stop words (停用词) 是在文档中频繁出现的，但是并没有携带太大的意义的词，不应该参与到运算中
  * stop words 是将输入的字符串（如分词器tokenizer的输出）中的停用词删除
  */

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.sql.{DataFrame, SparkSession}

object StopWordsRemoverExample {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName(s"${this.getClass.getSimpleName}")
      .getOrCreate()

    val sens:DataFrame = spark.createDataFrame(Seq(
      (0.0,"hi i header about Spark"),
      (0.0 ,"I wish Java could use case classes"),
      (1.0, "i Logistic regression models are neat")
    )).toDF("label" ,"sens")

    //进行分词
    val tokenizer: Tokenizer = new Tokenizer().setInputCol("sens").setOutputCol("word")
    tokenizer.transform(sens).show(false)
    val stopWord = new StopWordsRemover().setInputCol("word").setOutputCol("words")
    val wordData: DataFrame = stopWord.transform(tokenizer.transform(sens))
    wordData.show(100,false)
    val hashingTF: HashingTF = new HashingTF().setInputCol("words").setOutputCol("features").setNumFeatures(20)
    val idf: IDF = new IDF().setInputCol("features").setOutputCol("featu")
    val assembler = new VectorAssembler().setInputCols(Array("featu")).setOutputCol("feature")
    val lrPipeline=new Pipeline().setStages(Array(tokenizer,stopWord ,hashingTF ,idf ,assembler))
    val lrdata = lrPipeline.fit(sens)
    lrdata.transform(sens).show(false)


  }
}
