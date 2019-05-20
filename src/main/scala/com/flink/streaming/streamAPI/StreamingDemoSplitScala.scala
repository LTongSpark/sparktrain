package com.flink.streaming.streamAPI

import java.util

import com.flink.streaming.custormSource.MyNoParallelSourceScala
import org.apache.flink.streaming.api.collector.selector.OutputSelector
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}


/**
  */
object StreamingDemoSplitScala {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //隐式转换
    import org.apache.flink.api.scala._

    val text: DataStream[Long] = env.addSource(new MyNoParallelSourceScala)

    val splitStream = text.split(new OutputSelector[Long] {
      override def select(value: Long) = {
        val list = new util.ArrayList[String]()
        if(value%2 == 0){
          list.add("even")// 偶数
        }else{
          list.add("odd")// 奇数
        }
        list
      }
    })

    val split: DataStream[Long] = text.filter(_%2==0)

    val evenStream = splitStream.select("even")

//    evenStream.print().setParallelism(1)

    split.print()
    env.execute("StreamingDemoWithMyNoParallelSourceScala")



  }

}
