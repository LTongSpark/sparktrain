package com.flink.streaming.custormSource

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

/**
  *
  */
object StreamingDemoWithMyNoParallelSourceScala {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //隐式转换
    import org.apache.flink.api.scala._

    val text: DataStream[Long] = env.addSource(new MyNoParallelSourceScala)

    val mapData = text.map(line=>{
      println("接收到的数据："+line)
      line
    })

    val sum: DataStream[Long] = mapData.timeWindowAll(Time.seconds(2)).sum(0)


    sum.print().setParallelism(1)

    env.execute("StreamingDemoWithMyNoParallelSourceScala")



  }

}
