package com.flink.batch

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.configuration.Configuration

import scala.collection.mutable.ListBuffer

/**
  * broadcast 广播变量 相当于storm中的allgrouping
  */
object BatchDemoBroadcastScala {
  def main(args: Array[String]): Unit = {

    val env: ExecutionEnvironment = ExecutionEnvironment.getExecutionEnvironment

    import org.apache.flink.api.scala._

    //1: 准备需要广播的数据
    val broadData = ListBuffer[Tuple2[String, Int]]()
    broadData.append(("zs", 18))
    broadData.append(("ls", 20))
    broadData.append(("ww", 17))

    //1.1处理需要广播的数据转换为map
    val tupleData = env.fromCollection(broadData)
    val toBroadcastData = tupleData.map(tup => {
      Map(tup._1 -> tup._2)
    })


    val text = env.fromElements("tog", "ls", "ww")

    val result = text.map(new RichMapFunction[String, String] {

      //源码中是java的list
      var listData: java.util.List[Map[String, Int]] = null
      //转换为Scala中的list
      var allMap = Map[String, Int]()

      override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        //加载广播 的数据
        this.listData = getRuntimeContext.getBroadcastVariable[Map[String, Int]]("broadcastMapName")
        val it = listData.iterator()
        while (it.hasNext) {
          val next = it.next()
          allMap = allMap.++(next)
        }
      }

      override def map(value: String) = {
        val age = allMap.get(value).getOrElse(0)

        value + "," + age
      }
    }).withBroadcastSet(toBroadcastData, "broadcastMapName")


    result.print()

  }

}
