package com.example.opencvreadimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView;
    Button btnGray,btnNormal,btnLab;

    public static final String TAG = "Main";

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupUI();
        addListeners();
    }

    public void setupUI(){
        imageView = findViewById(R.id.imageView);
        btnGray = findViewById(R.id.btnGray);
        btnLab = findViewById(R.id.btnLuv);
        btnNormal = findViewById(R.id.btnNormal);
    }

    public void addListeners(){
        btnNormal.setOnClickListener(this);
        btnLab.setOnClickListener(this);
        btnGray.setOnClickListener(this);
    }

    private Mat readImageFromResources(int resourceId) {
        Mat imgToProcess = new Mat();

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resourceId);
        Utils.bitmapToMat(bmp, imgToProcess);

        return imgToProcess;
    }

    private void showImg(Mat img) {
        Bitmap bm = Bitmap.createBitmap(img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bm);
        imageView.setImageBitmap(bm);
    }

    public void changeColor(Mat mat, int colorConversionCode) {
        Imgproc.cvtColor(mat, mat, colorConversionCode);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        Mat mat = readImageFromResources(R.drawable.image);;
        switch (id){
            case R.id.btnGray:
                changeColor(mat,Imgproc.COLOR_BGR2GRAY);
                break;
            case R.id.btnLuv:
                changeColor(mat,Imgproc.COLOR_BGR2Luv);
                break;
            default:
                imageView.setImageResource(R.drawable.image);
                break;

        }
        showImg(mat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this,mOpenCVCallBack)){
            Log.e(TAG,"Cannot connect OpenCV Manager");
        }
    }
}
