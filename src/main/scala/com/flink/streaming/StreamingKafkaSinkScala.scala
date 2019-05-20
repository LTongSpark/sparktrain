package com.flink.streaming

import java.util.Properties
import java.util.concurrent.TimeUnit

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.time.Time
import org.apache.flink.contrib.streaming.state.RocksDBStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.environment.CheckpointConfig
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer010, FlinkKafkaProducer011}
import org.apache.flink.streaming.util.serialization.KeyedSerializationSchemaWrapper

/**
  * Created by xuwei.tech on 2018/10/23.
  */
object StreamingKafkaSinkScala {

  def main(args: Array[String]): Unit = {

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //隐式转换
    import org.apache.flink.api.scala._


    //checkpoint配置 ，设置checkpoint的检查点
    env.enableCheckpointing(5000);
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
    //确保检查点之间有至少500ms的间隔
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500);
    //检查点必须在一分钟内完成，或者被丢弃
    env.getCheckpointConfig.setCheckpointTimeout(60000);
    //同一时间只允许进行一个检查点
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1);
    //表示一旦程序呗close，会保留checkpoint的数据 ，以便根据实际需要回复这个数据
    env.getCheckpointConfig.enableExternalizedCheckpoints(CheckpointConfig
        .ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

    //设置statebackend ，保存在hdfs中

    env.setStateBackend(new RocksDBStateBackend("hdfs://hadoop100:9000/flink/checkpoints",true))

    //失败次数
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3,Time.of(10,TimeUnit.SECONDS)))


    val text = env.socketTextStream("hadoop100",9001,'\n')

    val brokerList = "hadoop110:9092"
    val topic = "t1"
    val prop = new Properties()
    prop.setProperty("bootstrap.servers","hadoop110:9092")
    //第一种解决方案，设置FlinkKafkaProducer011里面的事务超时时间
    //设置事务超时时间    务必要设置这个时间
    //prop.setProperty("transaction.timeout.ms",60000*15+"");

    //第二种解决方案，设置kafka的最大事务超时时间

    //val myProducer = new FlinkKafkaProducer010[String](brokerList, topic, new SimpleStringSchema());

    //使用支持仅一次语义的形式  只支持在kafka中11的版本中
    /**
      * 如果Flink开启了checkpoint，针对FlinkKafkaProducer09 和FlinkKafkaProducer010 可以提供 at-least-once的语义，还需要配置下面两个参数
      * setLogFailuresOnly(false)
      * setFlushOnCheckpoint(true)
      * 注意：建议修改kafka 生产者的重试次数
      * retries【这个参数的值默认是0】
      *
      *
      * 如果Flink开启了checkpoint，针对FlinkKafkaProducer011 就可以提供 exactly-once的语义
      * 但是需要选择具体的语义
      *Semantic.NONE
      *Semantic.AT_LEAST_ONCE【默认】
      *Semantic.EXACTLY_ONCE*
      *
      *
      */
    val myProducer = new FlinkKafkaProducer011[String](topic,
      new KeyedSerializationSchemaWrapper[String](new SimpleStringSchema()), prop,
      FlinkKafkaProducer011.Semantic.EXACTLY_ONCE)
    text.addSink(myProducer)

    env.execute("StreamingFromCollectionScala")



  }

}
