package com.flink.sql

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}

/**
  * Project: sparktrain
  * ClassName: com.flink.sql.SplitOperator
  * Version: V1.0
  *
  * Author: LTong
  * Date: 2019-07-01 下午 3:43
  */
object SplitOperator {
  def main(args: Array[String]): Unit = {
    val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    //创建数据集
    val dataStream1: DataStream[(String, Int)] = env.fromElements(("a", 1), ("d", 4))

    val splitedStream: SplitStream[(String, Int)] =  dataStream1.split(t =>{
      if(t._2%2 ==0) Seq("even")
      else Seq("odd")
    })

    val evenStream: DataStream[(String, Int)] = splitedStream.select("even")
    evenStream.print()

    env.execute(this.getClass.getSimpleName)

  }

}
