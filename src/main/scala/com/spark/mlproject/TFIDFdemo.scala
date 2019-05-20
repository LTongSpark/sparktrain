package com.spark.mlproject

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature._
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * tf-idf(term frequency-inverse document frequency) 一种广泛用于文本挖掘的特征向量方法
  * 用户反映术语对语料库中文档重要性，tf(Term Frequency):表示一个term与某个document的相关性，
  * idf(Inverse Document Frequency):表示一个term表示document的主题的权重大小，tf(t,d)词频，
  * idf(t,D)=log((|D|+1)/(DF(t,D)+1))，
  * 其中|D|表示文件集总数，DF词出现(t,D)文档数量，则tfidf(t,d,D)=tf(t,d)*idf(t,D)。
  * 示例： 一篇文件总词语是100个，词语“胡歌”出现了3次，那么“胡歌”一词在该文件中的词频TF(t,d)=3/100；
  * 如果“胡歌”一词在1000份文件中出现过，
  * 而文件总数是10000，其文件频率DF(t,D)=log((10000+1)/(1000+1)) *
  * 那么“胡歌”一词在该文件集的tf-idf分数为TF(t,d)*DF(t,D)。
  *
  */
/**
  * @author LTong
  * @date 2019-05-15 15:41
  */
object TFIDFdemo {
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

//    //生成特征向量
//    val features = hashingTF.transform(wordData)
//    print(hashingTF.getNumFeatures)
//    features.show(100,false)


    val lrPipeline=new Pipeline().setStages(Array(tokenizer,stopWord ,hashingTF ,idf ,assembler))
    val lrdata = lrPipeline.fit(sens)
    lrdata.transform(sens).show(false)

    //val model: IDFModel = idf.fit(features)


    //val result = model.transform(features)
    //result.show(100,false)

  }

}
