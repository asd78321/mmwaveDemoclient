package com.example.myapplication;

//引用 android類別庫
import androidx.appcompat.app.AppCompatActivity;


import java.lang.reflect.Array;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.widget.Button;

import android.view.View;

import android.widget.TextView;

import android.os.Bundle;


import org.tensorflow.lite.Interpreter;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import android.os.Handler;
import android.os.Looper;


import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.content.res.AssetFileDescriptor;



public class MainActivity extends AppCompatActivity {
    public static Handler mHandler = new Handler(Looper.getMainLooper());
    //介面文字端變數定義
    private Button start, stop;
    private TextView textView, x, y, z, raw_data, constate, IX, test_state,state;
    //連線端變數定義
    private Thread socket;
    private Socket clientSocket;//客戶端的socket
    private BufferedWriter bw;  //取得網路輸出串流
    private BufferedReader br;  //取得網路輸入串流
    private String tmp = "";     //做為接收時的緩存
    private String XID = "x";
    private String YID = "y";
    private String ZID = "z";
    private String numframes = "numframes";
    private Interpreter tflite;
    //    private ByteBuffer input1 = ByteBuffer.allocate(12 * 120 * 50 * 4);
    // 输出的结构
    private float[][] labelProbArray = null;
    // 取得網路資料
    private Runnable readData = new Runnable() {
        public void run() {
            // server端的IP
            InetAddress serverIp;
            try {
                // 以內定(本機電腦端)IP為Server端
                serverIp = InetAddress.getByName("192.168.56.1"); //10.0.2.2
                int serverPort = 5000;
                clientSocket = new Socket(serverIp, serverPort);

                // 取得網路輸入串流
                DataInputStream br = new DataInputStream(new BufferedInputStream(
                        clientSocket.getInputStream()));

                if (clientSocket.isConnected()) {
                    final String connect = "connected!!";
                    Runnable updataState = new Runnable() {
                        @Override
                        public void run() {
                            constate.setText(connect);
                        }
                    };
                    mHandler.post(updataState);
                }

                tflite = new Interpreter(loadModelFile("converted_model2"));
                float[][][] stack_pixel = new float[2][12][50 * 30];
                int count = 0;

                // 當連線後
                while (clientSocket.isConnected()) {
                    // 取得網路訊息
                    tmp = br.readLine();
//                    System.out.println(tmp);
                    // 如果不是空訊息則更新
                    if (tmp != null) {

//                        if (tmp.equals("UI DATA")) {
//                            tmp = br.readLine();
////                            System.out.println(tmp);
//                            try {
//                                JSONObject reader = new JSONObject(tmp);
//                                XID = reader.getString("x");
//                                YID = reader.getString("y");
//                                ZID = reader.getString("z");
//                                numframes = reader.getString("numframes");
//                                final String frames = numframes;
//                                final String X = XID;
//                                final String Y = YID;
//                                final String Z = ZID;
//                                Runnable updateText = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        IX.setText(X);
//                                        IY.setText(Y);
//                                        IZ.setText(Z);
//                                        raw_data.setText(frames);
//                                    }
//                                };
//                                mHandler.post(updateText);
//                            } catch (JSONException e) {
//                            }
//                        }
//Version 1 input for Server's parser
//                        if (tmp.equals("input")) {
//                            //input1
//                            int i1_len = br.readInt();
//                            byte[] i1 = new byte[i1_len * 4];
//                            br.readFully(i1, 0, i1_len * 4);
//                            FloatBuffer floatBuf1 = ByteBuffer.wrap(i1).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
//                            float[] array1 = new float[floatBuf1.remaining()];
//                            floatBuf1.get(array1);
//                            ByteBuffer input1=flBufTobyteBuf(array1,i1_len);
//
//
//
//
//                            //input2
//                            int i2_len = br.readInt();
//                            byte[] i2 = new byte[i2_len * 4];
//                            br.readFully(i2, 0, i2_len * 4);
//
//                            FloatBuffer floatBuf2 = ByteBuffer.wrap(i2).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
//                            float[] array2 = new float[floatBuf2.remaining()];
//                            floatBuf2.get(array2);
//                            ByteBuffer input2=flBufTobyteBuf(array2,i2_len);
//
//
//
////                            //input3
////                            int i3_len = br.readInt();
////                            byte[] i3 = new byte[i3_len * 4];
////                            br.readFully(i3, 0, i3_len * 4);
////
////                            FloatBuffer floatBuf3 = ByteBuffer.wrap(i3).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
////                            float[] array3 = new float[floatBuf3.remaining()];
////                            floatBuf3.get(array3);
////                            ByteBuffer input3=flBufTobyteBuf(array3,i3_len);
////                            float[][][][][] input3;
////                            input3=OnedimTomultidim(array3,12,120,120);
//
//
//                            //Object[] inputs = {input2 ,input3 ,input1}
//
//                            Object[] inputs = {input1,input2};
//                            Map<Integer, Object> outputs = new HashMap<>();
//                            final float[][] output_0 = new float[1][7];
//                            outputs.put(0, output_0);
//
//                            tflite.runForMultipleInputsOutputs(inputs,outputs);
//
////                            System.out.println(String.format("i1:%f,i2:%f,i3:%f",SumWithFloatArray(array1),SumWithFloatArray(array1),SumWithFloatArray(array1)));
////                            System.out.println(FindProbiIndex(output_0));
//
//                            Runnable updateText = new Runnable() {
//                                @Override
//                                public void run() {
//                                    state.setText(FindProbiIndex(output_0));
//                                }
//                            };
//                            mHandler.post(updateText);
//
//                        }
                        if (tmp.equals("raw")) {
//                            System.out.println(tmp);
                            int raw_len = br.readInt();
                            byte[] raw = new byte[raw_len];
                            br.readFully(raw, 0, raw_len);

                            final float[][] pointcloud = parseTLV(raw);


                            if(pointcloud !=null){
                                float[][] pixel = voxalize(pointcloud[0], pointcloud[1], pointcloud[2], 50, 30, 50);
                                stack_pixel = stackSlid_pixel(pixel, stack_pixel, count);
                                final int uiPointCount = pointcloud[0].length;
                                count+=1;
                                if (count > 12 && count % 3 == 1) {

                                    float[] input1 = flatteninput(stack_pixel[0]);
                                    float[] input2 = flatteninput(stack_pixel[1]);

                                    ByteBuffer byinput1 = flBufTobyteBuf(input1, input1.length);
                                    ByteBuffer byinput2 = flBufTobyteBuf(input2, input2.length);
                                    Object[] inputs = {byinput1, byinput2};

                                    Map<Integer, Object> outputs = new HashMap<>();
                                    final float[][] output_0 = new float[1][7];
                                    outputs.put(0, output_0);

                                    tflite.runForMultipleInputsOutputs(inputs, outputs);

                                    Runnable updateText = new Runnable() {
                                        @Override
                                        public void run() {
                                            state.setText(String.valueOf(FindProb(output_0)));
                                            test_state.setText(FindProbiIndex(output_0));
                                        }
                                    };
                                    mHandler.post(updateText);
                                }
                                Runnable updateTextUI = new Runnable() {
                                    @Override
                                    public void run() {
                                        IX.setText(String.valueOf(uiPointCount));
                                        raw_data.setText(String.valueOf(pointcloud[3][0]));
                                    }
                                };
                                mHandler.post(updateTextUI);
                            }
                        }
//                        if(tmp.equals("test")){
//                            int x_len = br.readInt();
//                            byte[] x = new byte[x_len * 4];
//                            br.readFully(x, 0, x_len * 4);
//                            FloatBuffer floatBufx = ByteBuffer.wrap(x).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
//                            float[] arrayx = new float[floatBufx.remaining()];
//                            floatBufx.get(arrayx);
//
//                            int y_len = br.readInt();
//                            byte[] y = new byte[y_len * 4];
//                            br.readFully(y, 0, y_len * 4);
//                            FloatBuffer floatBufy = ByteBuffer.wrap(y).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
//                            float[] arrayy = new float[floatBufy.remaining()];
//                            floatBufy.get(arrayy);
//
//                            int z_len = br.readInt();
//                            byte[] z = new byte[z_len * 4];
//                            br.readFully(z, 0, z_len * 4);
//                            FloatBuffer floatBufz = ByteBuffer.wrap(z).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
//                            float[] arrayz = new float[floatBufz.remaining()];
//                            floatBufz.get(arrayz);
//
//                            float pixel[][] = voxalize(arrayx,arrayy,arrayz,50,30,50);
//                            stack_pixel=stackSlid_pixel(pixel,stack_pixel,count);
//                            count+=1;
//                            if(count>12 && count%3==1){
//
//                                float []input1 = flatteninput(stack_pixel[0]);
//                                float []input2 = flatteninput(stack_pixel[1]);
//
//                                ByteBuffer byinput1=flBufTobyteBuf(input1,input1.length);
//                                ByteBuffer byinput2=flBufTobyteBuf(input2,input2.length);
//
//                                Object[] inputs = {byinput1,byinput2};
//
//                                Map<Integer, Object> outputs = new HashMap<>();
//                                final float[][] output_0 = new float[1][7];
//                                outputs.put(0, output_0);
//
//                                tflite.runForMultipleInputsOutputs(inputs,outputs);
//
//                                Runnable updateText = new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        test_state.setText(FindProbiIndex(output_0));
//                                    }
//                                };
//                                mHandler.post(updateText);
//                            }
//                        }
                    }
                }
            } catch (IOException e) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //描述介面文字
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        //  push_txt=(Button)findViewById(R.id.push_txt);
        textView = (TextView) findViewById(R.id.textview);
        constate = (TextView) findViewById(R.id.constate);
//        x = (TextView) findViewById(R.id.data_x);
//        y = (TextView) findViewById(R.id.data_y);
//        z = (TextView) findViewById(R.id.data_z);
        IX = (TextView) findViewById(R.id.X_ID);
        raw_data = (TextView) findViewById(R.id.raw_data);
        test_state = (TextView) findViewById(R.id.state2);
        state=(TextView)findViewById(R.id.state);
        // final EditText Edtxt=(EditText)findViewById(R.id.Edtxt);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(R.string.start_button);
                socket = new Thread(readData);
                test_state.setText("waiting...");
                socket.start();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textView.setText("waiting connect");
                constate.setText("disconenct!!");

                try {
                    test_state.setText("");
                    IX.setText("");

                    raw_data.setText("0");
                    clientSocket.close();
                    socket.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


    float[][][][][] OnedimTomultidim(float flbuf[], int x, int y, int z) {
        int count = 0;
        float[][][][][] input1 = new float[1][x][y][z][1];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int q = 0; q < z; q++) {

                    input1[0][i][j][q][0] = flbuf[count];
                }
            }
        }
        return input1;
    }

    float[][] parseTLV(byte[] fileBytes) {
        byte[] magic_word = {(byte) 0x02, (byte) 0x01, (byte) 0x04, (byte) 0x03, (byte) 0x06, (byte) 0x05, (byte) 0x08, (byte) 0x07};
        int FrameLengthReal = fileBytes.length;
        int Headerlength = 60;
        int PointCloudlenght = 20;


        for (int i = 0; i < 8; i++) {
            if (fileBytes[i] == magic_word[i]) {
                continue;
            } else {
                return null;
            }
        }
        byte[] bytesubFrame = Arrays.copyOfRange(fileBytes, 24, 28);
        int subFrameNumber = ByteArray2Int(bytesubFrame);

        byte[] bytetypeTLV = Arrays.copyOfRange(fileBytes, 52, 56);
        int typeTLV = ByteArray2Int(bytetypeTLV);

        byte[] bytelenTLV = Arrays.copyOfRange(fileBytes, 56, 60);
        int lenTLV = ByteArray2Int(bytelenTLV);

//        if(lenTLV>FrameLengthReal-60){
//            return null;
//        }
        if(lenTLV-8>fileBytes.length-60){
            System.out.println(lenTLV-fileBytes.length);
            return null;
        }

        int point_count = (lenTLV - 8) / 20;
        float[][] point_cloud = new float[4][point_count];// XYZ+frames

        for (int i = 0; i < point_count; i++) {
            byte[] Byterange = Arrays.copyOfRange(fileBytes, PointCloudlenght * i + Headerlength, PointCloudlenght * i + Headerlength + 4);
            float range = ByteBuffer.wrap(Byterange).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            byte[] Byteazimuth = Arrays.copyOfRange(fileBytes, PointCloudlenght * i + Headerlength + 4, PointCloudlenght * i + Headerlength + 8);
            float azimuth = ByteBuffer.wrap(Byteazimuth).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            byte[] Byteelevation = Arrays.copyOfRange(fileBytes, PointCloudlenght * i + Headerlength + 8, PointCloudlenght * i + Headerlength + 12);
            float elevation = ByteBuffer.wrap(Byteelevation).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            byte[] Bytedoppler = Arrays.copyOfRange(fileBytes, PointCloudlenght * i + Headerlength + 12, PointCloudlenght * i + Headerlength + 16);
            float doppler = ByteBuffer.wrap(Bytedoppler).order(ByteOrder.LITTLE_ENDIAN).getFloat();

            point_cloud[0][i] = (float) (range * Math.cos(elevation) * Math.sin(azimuth));
            point_cloud[1][i] = (float) (range * Math.cos(elevation) * Math.cos(azimuth));
            point_cloud[2][i] = (float) (range * Math.sin(elevation));
        }
        point_cloud[3][0]=subFrameNumber;
        return point_cloud;
    }



        float[][] voxalize ( float x[], float y[], float z[], int pointX, int pointY, int pointZ){
            int len = x.length;

//        float [] pixel1 = new float[pointX * pointY];
//        float [] pixel2 = new float[pointY * pointZ];
            float[][] pixel = new float[2][pointX * pointZ];

            int x_min = -3;
            int x_max = 3;

            int y_min = 0;
            double y_max = 2.5;

            int z_max = 3;
            int z_min = -3;


            double x_res = (double) (x_max - x_min) / (pointX);
            double y_res = (double) (y_max - y_min) / pointY;
            double z_res = (double) (z_max - z_min) / pointZ;


            for (int i = 0; i < len; i++) {

                double x_pix = Math.floor((x[i] - (double) (x_min)) / x_res);
                double y_pix = Math.floor((y[i] - (double) (y_min)) / y_res);
                double z_pix = Math.floor((z[i] - (double) (z_min)) / z_res);


                if (x_pix > pointX) {
                    continue;
                }
                if (y_pix > pointY) {
                    continue;
                }
                if (z_pix > pointZ) {
                    continue;
                }

                if (x_pix == pointX) {
                    x_pix = pointX - 1;
                }
                if (y_pix == pointY) {
                    y_pix = pointY - 1;
                }
                if (z_pix == pointZ) {
                    z_pix = pointZ - 1;
                }

                int countx = (int) ((y_pix) * (pointX) + x_pix);
                int county = (int) ((y_pix) * (pointZ) + z_pix);

                if (countx > 0 && county > 0) {
                    pixel[0][countx] += 1;
                    pixel[1][county] += 1;
                } else {
                    continue;
                }


            }
            return pixel;
        }
        float[] flatteninput ( float stack_pixel[][]){
            float[] input = new float[12 * 50 * 30];
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < (50 * 30); j++) {
                    input[(i * 50 * 30) + j] = stack_pixel[i][j];
                }
            }
            return input;
        }
        float[][][] stackSlid_pixel ( float pixel[][], float stack_pixel[][][], int frame_count){
            if (frame_count < 12) {
                stack_pixel[0][frame_count] = pixel[0];
                stack_pixel[1][frame_count] = pixel[1];
            } else {
                float[][][] new_stack_pixel = new float[2][12][50 * 30];
                for (int i = 0; i < 11; i++) {
                    new_stack_pixel[0][i] = stack_pixel[0][i + 1];  //third dim is for [X*Y points]
                    new_stack_pixel[1][i] = stack_pixel[1][i + 1];
                }
                new_stack_pixel[0][11] = pixel[0];
                new_stack_pixel[1][11] = pixel[1];

                stack_pixel = new_stack_pixel;
            }

            return stack_pixel;
        }


        ByteBuffer flBufTobyteBuf ( float flbuf[], int len){
            ByteBuffer buffer = ByteBuffer.allocateDirect(len * 4);
            buffer.order(ByteOrder.nativeOrder());
            buffer.rewind();
            for (int i = 0; i < len; i++) {
                buffer.putFloat(flbuf[i]);
            }
            return buffer;
        }

        String FindProbiIndex ( float probArray[][]){
        String[] classes = {"st_sit", "sit_st", "sit_lie", "lie_sit", "fall", "grow_up", "other"};
        float temp = probArray[0][0];
        int key = 0;

        for (int count = 1; count < 7; count++) {
            if (temp < probArray[0][count]) {
                temp = probArray[0][count];
                key = count;
            }
        }
        return classes[key];
    }

    float FindProb ( float probArray[][]){
        float temp = probArray[0][0];
        int key = 0;

        for (int count = 1; count < 7; count++) {
            if (temp < probArray[0][count]) {
                temp = probArray[0][count];
                key = count;
            }
        }
//            System.out.println(probArray[0][key]);
        return probArray[0][key];
    }

        float SumWithFloatArray ( float flbuf[]){
            float sum = 0;
            for (float value : flbuf) {
                sum += value;
            }
            return sum;
        }
        public static int ByteArray2Int ( byte[] b){
            int MASK = 0xFF;
            int result = 0;
            result = b[0] & MASK;
            result = result + ((b[1] & MASK) << 8);
            result = result + ((b[2] & MASK) << 16);
            result = result + ((b[3] & MASK) << 24);
            return result;
        }

        private MappedByteBuffer loadModelFile (String model) throws IOException {
            AssetFileDescriptor fileDescriptor = getApplicationContext().getAssets().openFd(model + ".tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }
