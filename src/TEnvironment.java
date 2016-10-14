import scala.Tuple2;

import java.io.*;
import java.util.Arrays;

public class TEnvironment {
    //状態数
    private int fObservationSize;

    //現在の状態
    private double[] fObservation;

    //次の状態
    private double[] fNextObservation;

    //プロセスビルダー
    private ProcessBuilder fProcessBuilder;

    //プロセス
    private Process fProcess;

    //Javaからpythonに書き込むStream
    private BufferedWriter fBufferedWriter;

    //Pythonから読み込むStream
    private BufferedReader fBufferedReader;

    /**
     * プロセス指定コンストラクタ
     * @param pathOfPythonFile
     * @param pathOfPython
     * @param observationSize
     * @throws IOException
     */
    public TEnvironment(String pathOfPythonFile, String pathOfPython, int observationSize) throws IOException {

        fObservationSize = observationSize;
        fObservation = new double[fObservationSize];
        fNextObservation = new double[fObservationSize];

        //プロセスビルダー作成(1.動かすPythonファイルの場所, 2.Pythonのライブラリなどの場所)
        fProcessBuilder = new ProcessBuilder(Arrays.asList(pathOfPython, pathOfPythonFile));

        //プロセス起動
        fProcess = fProcessBuilder.start();

        //Pythonに書き込むStream
        fBufferedWriter = new BufferedWriter(new OutputStreamWriter(fProcess.getOutputStream()));

        //Pythonから読み込むStream
        fBufferedReader = new BufferedReader(new InputStreamReader(fProcess.getInputStream()));
    }

    /**
     * 環境の初期化
     * @return
     */
    public double[] initializeState() throws IOException {

        //環境の初期化関数の呼び出し
        fBufferedWriter.write("init_state" + "\n");
        fBufferedWriter.flush();

        //帰ってきた初期状態の読み込み
        String read = fBufferedReader.readLine();
        readObservation(read);

        return fObservation;
    }

    /**
     * 1行動実行
     * @param action
     * @return
     * @throws IOException
     */
    public Tuple2<Double, Boolean> doAction(int action) throws IOException, InterruptedException {

        //1行動実行関数の呼び出し
        //送りたい1行ごとに改行が必要
        fBufferedWriter.write("do_action" + "\n");
        fBufferedWriter.write(observationToString() + "\n" + action + "\n");
        fBufferedWriter.flush();

        //
        //プロセスが動く
        //

        //新しい環境の受け取り
        String read = fBufferedReader.readLine();
        readNextobservation(read);

        //報酬の受け取り
        String reward = fBufferedReader.readLine();

        //終了判定の受け取り
        String done = fBufferedReader.readLine();

        return new Tuple2<>(Double.parseDouble(reward), Boolean.parseBoolean(done));

    }

    /**
     * 1エピソード実行
     * @return
     * @throws IOException
     */
    public double doOneEpisode() throws IOException {
        //1エピソード実行関数の呼び出し
        fBufferedWriter.write("do_one_episode" + "\n");
        fBufferedWriter.flush();

        //
        //プロセスが動く
        //

        //報酬の受け取り
        String reward = fBufferedReader.readLine();

        return Double.parseDouble(reward);
    }

    /**
     * プロセス終了
     * @throws IOException
     */
    public void quitProcess() throws IOException {
        //ループを抜ける関数の呼び出し
        fBufferedWriter.write("quit" + "\n");
        fBufferedWriter.flush();

        fBufferedWriter.close();
        fBufferedWriter.close();
    }

    /**
     * 環境の状態を取得
     * @param observationString
     */
    private void readObservation(String observationString){
        String[] splitObStr = observationString.split(" ");
        assert fObservation.length == splitObStr.length;
        for (int i = 0; i < splitObStr.length; i++) {
            fObservation[i] = Double.parseDouble(splitObStr[i]);
        }
    }

    /**
     * 次の環境の状態を取得
     * @param nextObservationString
     */
    private void readNextobservation(String nextObservationString){
        String[] splitObStr = nextObservationString.split(" ");
        assert fNextObservation.length == splitObStr.length;
        for (int i = 0; i < splitObStr.length; i++) {
            fNextObservation[i] = Double.parseDouble(splitObStr[i]);
        }
    }

    /**
     * 環境の状態をStringに変換
     * @return
     */
    private String observationToString(){
        assert fObservation.length == fObservationSize;

        String observationString = "";
        for (int i = 0; i < fObservationSize; i++) {
            observationString += fObservation[i] + " ";
        }
        return observationString;
    }

    /**
     * 環境の状態の取得
     * @return
     */
    public double[] getObservation(){
        return fObservation;
    }

    /**
     * 環境の更新
     */
    public void updateObservation(){
        assert fObservationSize == fObservation.length && fObservationSize == fNextObservation.length;
        for(int i = 0; i < fObservationSize; i++) {
            fObservation[i] = fNextObservation[i];
        }
    }

}
