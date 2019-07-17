package com.flink.batch

import org.apache.flink.api.java.functions.FunctionAnnotation.NonForwardedFields
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._
import org.apache.spark.api.java.function.MapFunction

/**
  * Project: sparktrain
  * ClassName: com.flink.batch.FIeldAnnotation
  * Version: V1.0
  *
  * Author: LTong
  * Date: 2019-07-04 上午 10:03
  */
object FIeldAnnotation {
  def main(args: Array[String]): Unit = {
    val env = ExecutionEnvironment.getExecutionEnvironment

    val data1= env.fromElements(("foo" ,1),("tong" ,2))
    val data2 = env.fromElements((12,23) ,(23,24))

//    @NonForwardedFields("_2;_3")
//     class mymapper1 extends MapFunction[(String ,Long ,Int) ,(String ,Long ,Int)]{
//      def map(input: (String, Long, Int)): (String, Long, Int) = {
//        return (input._1, input._2 / 2, input._3 * 4)
//      }
//      override def call(value: (String, Long, Int)): (String, Long, Int) = ???
//    }

  }


}
