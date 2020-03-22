package com.bolingcavalry.customize;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;

/**
 * @author will
 * @email zq2599@gmail.com
 * @date 2020-03-21 16:35
 * @description 最简单的自定义数据源的实战：实现SourceFunction接口
 */
public class SourceFunctionDemo {
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //并行度为1
        env.setParallelism(2);

        DataStream<Tuple2<Integer,Integer>> dataStream = env.addSource(new SourceFunction<Tuple2<Integer, Integer>>() {

            private volatile boolean isRunning = true;

            @Override
            public void run(SourceContext<Tuple2<Integer, Integer>> ctx) throws Exception {
                int i = 0;
                while (isRunning) {
                    ctx.collect(new Tuple2<>(i++ % 5, 1));
                    Thread.sleep(1000);
                    if(i>9){
                        break;
                    }
                }
            }

            @Override
            public void cancel() {
                isRunning = false;
            }
        });

        dataStream
                .keyBy(0)
                .timeWindow(Time.seconds(2))
                .sum(1)
                .print();

        env.execute("Customize DataSource demo : SourceFunction");
    }
}