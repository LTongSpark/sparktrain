
package com.spark.feature_extraction
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}
import org.apache.spark.sql.SparkSession

/**
  * idf(Inverse Document Frequency):表示一个term表示document的主题的权重大小，tf(t,d)词频，
  * * idf(t,D)=log((|D|+1)/(DF(t,D)+1))，
  * * 其中|D|表示文件集总数，DF词出现(t,D)文档数量，则tfidf(t,d,D)=tf(t,d)*idf(t,D)。
  * * 示例： 一篇文件总词语是100个，词语“胡歌”出现了3次，那么“胡歌”一词在该文件中的词频TF(t,d)=3/100；
  * * 如果“胡歌”一词在1000份文件中出现过，
  * * 而文件总数是10000，其文件频率DF(t,D)=log((10000+1)/(1000+1)) *
  * * 那么“胡歌”一词在该文件集的tf-idf分数为TF(t,d)*DF(t,D)。
  *
  * 1、TF-IDF(词频-逆文档频率)
  * 逆文档频率是一个数字量度，表示一个单词提供了多少信息。
  * 词频（Term Frequency，TF）指的是某一个给定的词语在该文件中出现的频率。这个数字是对词数(term count)的归一化，
  * 以防止它偏向长的文件。（同一个词语在长文件里可能会比短文件有更高的词数，而不管该词语重要与否。）
  * 逆向文件频率（Inverse Document Frequency，IDF）是一个词语普遍重要性的度量。某一特定词语的IDF，
  * 可以由总文件数目除以包含该词语之文件的数目，再将得到的商取对数得到。
  *
  * tf-idf模型
  * 目前，真正在搜索引擎等实际应用中广泛使用的是tf-idf模型。tf-idf模型的主要思想是：
  * 如果词w在一篇文档d中出现的频率高，并且在其他文档中很少出现，则认为词w具有很好的区分能力，适合用来把文章d和其他文章区分开来。
  *
  * TF：HashingTF和CountVectorizer都可以用来生成词频向量。
  * HashingTF是一个转换器，它接受词集合输入，并将这些集合转换为固定长度的特征向量。
  * 在文本处理中，一个词语集合也许是一个词袋。HashingTF利用哈希技巧。原始特征被映射到索引通过使用Hash函数。
  * Hash函数使用MurmurHash 3,然后根据映射的索引计算词频。这个方法避免了在大语料上成本昂贵的全局词索引map的计算方式，
  * 但是会存在潜在的hash冲突，也就是不同的词特征在hash后被映射成相同的词。为了减少冲突的机会，我们可以增加目标特征维度，
  * 即散列表的桶数。由于使用简单的模数将散列函数转换为列索引，建议使用两个幂作为特征维，否则不会将特征均匀地映射到列。
  * 默认的特征维度为2^18=262,144.可选的二进制切换参数控制词频计数。当设置为true时，所有非零频率计数设置为1.
  * 对于模拟二进制而不是整数计数的离散概率模型特别有用。
  * CountVectorizer 将一个文本文档转成词数向量，具体参考CountVectorizer
  * IDF:IDF是一个估计器，通过拟合数据集产生一个IDFModel。IDFModel输入特征向量（通过由HashingTF 或者 CountVectorizer创建）
  * 并且缩放每列。直观地说，它减少了在语料库中频繁出现的列。
  *
  */
object TfIdfExample {

  def main(args: Array[String]) {
    val spark = SparkSession
      .builder
        .master("local[*]")
      .appName("TfIdfExample")
      .getOrCreate()

    val sentenceData = spark.createDataFrame(Seq(
      (0.0, "Hi I heard about Spark"),
      (0.0, "I wish Java could use case classes"),
      (1.0, "Logistic regression models are neat")
    )).toDF("label", "sentence")

    val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
    val wordsData = tokenizer.transform(sentenceData)

    val hashingTF = new HashingTF()
      .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)

    val featurizedData = hashingTF.transform(wordsData)

    val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
    val idfModel = idf.fit(featurizedData)

    val rescaledData = idfModel.transform(featurizedData)
    rescaledData.select("label", "features").show()

    spark.stop()
  }
}
