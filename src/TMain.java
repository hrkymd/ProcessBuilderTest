import scala.Tuple2;

import java.io.*;
import java.util.Random;

public class TMain {
    public static void main(String[] args) throws IOException, InterruptedException {

        Random random = new Random();

        //パスの指定
        String pathOfPythonFile = ".src/cartPole.py";
        String pathOfPython = "ENTER_YOUR_PYTHON_PATH";

        //観測できる個数
        int observationSize = 4;

        //環境の作成
        TEnvironment env = new TEnvironment(pathOfPythonFile, pathOfPython, observationSize);

        //環境の初期化
        env.initializeState();

        //200行動実行
        int actionCount = 200;
        double total_reward = 0.0;
        for(int i = 0; i < actionCount; i++){
            //行動生成(carPole:0か1の乱数生成)
            int action = (int)Math.round(random.nextDouble());

            //1行動実行
            Tuple2<Double, Boolean> result = env.doAction(action);

            //ScalaのTupleは._1 ._2などで要素を取得できる.
            //合計報酬の計算
            total_reward += result._1;

            //終了判定
            if(result._2){
                System.out.println("done");
                break;
            }

            //次の状態に環境の更新
            env.updateObservation();
        }
        System.out.println(total_reward);

        //1エピソード実行
        double reward =  env.doOneEpisode();
        System.out.println(reward);

        //プロセス終了
        env.quitProcess();
    }
}
