package com.spark.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author LTong
  * @date 2019-05-15 16:37
  */
object DataCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName(s"${this.getClass.getSimpleName}")
      .setMaster("local[*]")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val sc = new SparkContext(conf)

    val text: RDD[String] = sc.textFile("")

    val wordcount = text.flatMap(_.split(" ")).map((_,1)).reduceByKey(_+_).coalesce(2)

    wordcount.collect().foreach(print)

    print("单独文件中各个数的统计个数")
    wordcount.map(_._2).foreach(print)

    print("获取统计的最大数")
    wordcount.sortBy(_._2 ,false).take(1)

    print("转变为key-value的形式")

    var map:Map[String,Int] = null
    wordcount.map(rdd =>{
      val word = rdd._1
      val count = rdd._2
      map += (word -> count)
    }).foreach(print)
  }

}
