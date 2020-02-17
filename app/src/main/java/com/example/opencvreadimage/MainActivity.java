package com.example.opencvreadimage;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ImageView imageView;
    Spinner spinner;

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
        populateSpinner();
        addListeners();
    }

    public void setupUI() {
        imageView = findViewById(R.id.imageView);
        spinner = findViewById(R.id.spinnerEffects);
    }

    public void addListeners() {
        spinner.setOnItemSelectedListener(this);
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
        //set image bitmap below
        imageView.setImageBitmap(bm);
    }

    public void changeColor(Mat mat, int colorConversionCode) {
        Imgproc.cvtColor(mat, mat, colorConversionCode);
    }


    public void populateSpinner() {
        Spinner spinner = findViewById(R.id.spinnerEffects);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.imageEffects, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallBack)) {
            Log.e(TAG, "Cannot connect OpenCV Manager");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        applyEffect(position);
    }

    private void applyEffect(int position) {
        Mat src;
        src = readImageFromResources(R.drawable.lenna);
        Size kernel = new Size(71, 19);
        switch (position) {
            case ImageEffects.NORMAL:
                //Do nothing
                break;
            case ImageEffects.MEAN:
                Imgproc.blur(src,src,kernel);
                break;
            case ImageEffects.MEDIAN:
                Imgproc.medianBlur(src,src,71);
                break;
            case ImageEffects.GUASSIAN:
                Imgproc.GaussianBlur(src,src,kernel,0);
                break;
            case ImageEffects.THRESHOLD:
                Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
                Imgproc.threshold(src,src,100,255,Imgproc.THRESH_BINARY);
                break;
            case ImageEffects.ADAPTIVE:
                Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
                Imgproc.adaptiveThreshold(src,src,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,3,0);
                break;
            case ImageEffects.EROSION:
                Mat kernelErose = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,kernel);
                Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
                Imgproc.erode(src,src,kernelErose);
                break;
            case ImageEffects.DILATION:
                Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,kernel);
                Imgproc.cvtColor(src,src,Imgproc.COLOR_BGR2GRAY);
                Imgproc.dilate(src,src,kernelDilate);
                break;
            default:
                break;
        }

        //Use showImage to set the processedImage to imageview
        showImg(src);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
    }

    private static class ImageEffects {
        static final int NORMAL = 0, MEAN = 1, MEDIAN = 2, GUASSIAN = 3, THRESHOLD = 4, ADAPTIVE = 5, EROSION = 6, DILATION = 7;
    }
}
