package com.example.androidcameraexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static android.media.ExifInterface.TAG_GPS_LATITUDE;
import static android.media.ExifInterface.TAG_GPS_LATITUDE_REF;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE;
import static android.media.ExifInterface.TAG_GPS_LONGITUDE_REF;
import static android.media.ExifInterface.TAG_IMAGE_LENGTH;
import static android.media.ExifInterface.TAG_MAKE;
import static android.media.ExifInterface.TAG_MODEL;


public class MainActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback {
    final static int REQUEST_IMAGE_CAPTURE = 1111;
    private static final String TAG = "android_camera_example";
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION};
    private static final int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_BACK; // Camera.CameraInfo.CAMERA_FACING_FRONT

    private SurfaceView surfaceView;
    private CameraPreview mCameraPreview;
    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)
    private TextView mView;
    public String imageFilePath;
    String filename = Environment.getExternalStorageDirectory().getPath() ;
    String fileName = String.format("%d.JPEG", System.currentTimeMillis());
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 상태바를 안보이도록 합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 화면 켜진 상태를 유지합니다.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mLayout = findViewById(R.id.layout_main);
        surfaceView = findViewById(R.id.camera_preview_main);


        // 런타임 퍼미션 완료될때 까지 화면에서 보이지 않게 해야합니다.
        surfaceView.setVisibility(View.GONE);

        Button button = findViewById(R.id.button_main_capture);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCameraPreview.takePicture();

            }
        });
        Button button1 = findViewById(R.id.button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraPreview.takePicture();
            }
        });
       mView =  findViewById(R.id.textView);

/*
         String filename = Environment.getExternalStorageDirectory().getPath() ;
        String fileName = String.format("%d.JPEG", System.currentTimeMillis());
        try {
            ExifInterface exif = new ExifInterface(filename+fileName);
            exif.saveAttributes();
           EExif(exif);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error!", Toast.LENGTH_LONG).show();
            Log.d("로그","에러");
        }

*/

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            int locasionPermission =ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
            int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if ( cameraPermission == PackageManager.PERMISSION_GRANTED
                    && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED
            && locasionPermission == PackageManager.PERMISSION_GRANTED)  {
                startCamera();


            }else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Snackbar.make(mLayout, "이 앱을 실행하려면 카메라와 외부 저장소 접근 권한이 필요합니다.",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions( MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
                    }
                    }).show();


                } else {
                    // 2. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                    // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                    ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                            PERMISSIONS_REQUEST_CODE);
                }

            }

        } else {

            final Snackbar snackbar = Snackbar.make(mLayout, "디바이스가 카메라를 지원하지 않습니다.",
                    Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("확인", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }


    }



    void startCamera(){

        // Create the Preview view and set it as the content of this Activity.
        mCameraPreview = new CameraPreview(this, this, CAMERA_FACING, surfaceView);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_IMAGE_CAPTURE && requestCode == RESULT_OK){
            Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
            ExifInterface exif = null;
            exif.setAttribute(TAG_GPS_LATITUDE,"33");
            exif.setAttribute(TAG_GPS_LATITUDE_REF,"33");
            exif.setAttribute(TAG_GPS_LONGITUDE,"33");
            exif.setAttribute(TAG_GPS_LONGITUDE_REF,"33");
            exif.setAttribute(TAG_IMAGE_LENGTH,"33");
            exif.setAttribute(TAG_MODEL,"33");
            exif.setAttribute(TAG_MAKE,"33");
            try {
                exif.saveAttributes();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"씨발 또안대냐",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            try{
                exif = new ExifInterface(imageFilePath); // 이미지의 정보를 생성.

            }catch(IOException e){
                e.printStackTrace();
            }

            int exifOrientation;
            int exifDegree;
            if(exif!=null){
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);

            } else{
                exifDegree=0;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {

        if ( requestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {

                startCamera();
            }
            else {

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {

                    Snackbar.make(mLayout, "설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }



    }

    /*public void EExif(ExifInterface exif) {

        String myAttribute = "[Exif information] \n\n";

        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,"33");
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"33");
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,"33");
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,"33");
        exif.setAttribute(ExifInterface.TAG_IMAGE_LENGTH,"33");
        exif.setAttribute(ExifInterface.TAG_MODEL,"33");
        exif.setAttribute(ExifInterface.TAG_MAKE,"33");
        myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
        myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
        myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
        myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
        myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
        mView.setText(myAttribute);
    }

     String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }


*/
  /*  private ExifInterface e;

    private String imagePath;
    public void eraseMetadata() throws IOException {
        try {
            e = new ExifInterface(imagePath);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Log.d("Apertura: ", ExifInterface.TAG_DATETIME);


        e.setAttribute(ExifInterface.TAG_APERTURE_VALUE, "3,2");
        e.setAttribute(ExifInterface.TAG_ARTIST, null);
        e.setAttribute(ExifInterface.TAG_BITS_PER_SAMPLE,null);
        e.setAttribute(ExifInterface.TAG_BRIGHTNESS_VALUE,null);
        e.setAttribute(ExifInterface.TAG_CFA_PATTERN ,null);
        e.setAttribute(ExifInterface.TAG_COLOR_SPACE ,null);
        e.setAttribute(ExifInterface.TAG_COMPONENTS_CONFIGURATION ,null);
        e.setAttribute(ExifInterface.TAG_COMPRESSED_BITS_PER_PIXEL ,null);
        e.setAttribute(ExifInterface.TAG_COMPRESSION ,null);
        e.setAttribute(ExifInterface.TAG_CONTRAST ,null);
        e.setAttribute(ExifInterface.TAG_COPYRIGHT ,"Mariotepro");
        e.setAttribute(ExifInterface.TAG_CUSTOM_RENDERED ,null);
        e.setAttribute(ExifInterface.TAG_DATETIME ,null);
        e.setAttribute(ExifInterface.TAG_DATETIME_DIGITIZED ,null);
        e.setAttribute(ExifInterface.TAG_DATETIME_ORIGINAL ,null);
        e.setAttribute(ExifInterface.TAG_DEVICE_SETTING_DESCRIPTION ,null);
        e.setAttribute(ExifInterface.TAG_DIGITAL_ZOOM_RATIO ,null);
        e.setAttribute(ExifInterface.TAG_EXIF_VERSION ,null);
        e.setAttribute(ExifInterface.TAG_EXPOSURE_BIAS_VALUE ,null);
        e.setAttribute(ExifInterface.TAG_EXPOSURE_INDEX ,null);
        e.setAttribute(ExifInterface.TAG_EXPOSURE_MODE ,null);
        e.setAttribute(ExifInterface.TAG_EXPOSURE_PROGRAM ,null);
        e.setAttribute(ExifInterface.TAG_EXPOSURE_TIME ,null);
        e.setAttribute(ExifInterface.TAG_F_NUMBER ,null);
        e.setAttribute(ExifInterface.TAG_FILE_SOURCE ,null);
        e.setAttribute(ExifInterface.TAG_FLASH ,null);
        e.setAttribute(ExifInterface.TAG_FLASH_ENERGY ,null);
        e.setAttribute(ExifInterface.TAG_FLASHPIX_VERSION ,null);
        e.setAttribute(ExifInterface.TAG_FOCAL_LENGTH ,null);
        e.setAttribute(ExifInterface.TAG_FOCAL_LENGTH_IN_35MM_FILM ,null);
        e.setAttribute(ExifInterface.TAG_FOCAL_PLANE_RESOLUTION_UNIT ,null);
        e.setAttribute(ExifInterface.TAG_FOCAL_PLANE_X_RESOLUTION ,null);
        e.setAttribute(ExifInterface.TAG_FOCAL_PLANE_Y_RESOLUTION ,null);
        e.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT ,null);
        e.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH ,null);
        e.setAttribute(ExifInterface.TAG_GAIN_CONTROL ,null);
        e.setAttribute(ExifInterface.TAG_GPS_ALTITUDE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_AREA_INFORMATION ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DATESTAMP ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_BEARING_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_DISTANCE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_DISTANCE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_LATITUDE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_LONGITUDE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DEST_LONGITUDE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_LONGITUDE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_LATITUDE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DIFFERENTIAL ,null);
        e.setAttribute(ExifInterface.TAG_GPS_DOP ,null);
        e.setAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION ,null);
        e.setAttribute(ExifInterface.TAG_GPS_IMG_DIRECTION_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_MAP_DATUM ,null);
        e.setAttribute(ExifInterface.TAG_GPS_MEASURE_MODE ,null);
        e.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD ,null);
        e.setAttribute(ExifInterface.TAG_GPS_SATELLITES ,null);
        e.setAttribute(ExifInterface.TAG_GPS_SPEED ,null);
        e.setAttribute(ExifInterface.TAG_GPS_SPEED_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_STATUS ,null);
        e.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP ,null);
        e.setAttribute(ExifInterface.TAG_GPS_TRACK ,null);
        e.setAttribute(ExifInterface.TAG_GPS_TRACK_REF ,null);
        e.setAttribute(ExifInterface.TAG_GPS_VERSION_ID ,null);
        e.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION ,null);
        e.setAttribute(ExifInterface.TAG_IMAGE_LENGTH ,null);
        e.setAttribute(ExifInterface.TAG_IMAGE_UNIQUE_ID ,null);
        e.setAttribute(ExifInterface.TAG_IMAGE_WIDTH ,null);
        e.setAttribute(ExifInterface.TAG_INTEROPERABILITY_INDEX ,null);
        e.setAttribute(ExifInterface.TAG_ISO_SPEED_RATINGS ,null);
        e.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT ,null);
        e.setAttribute(ExifInterface.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH ,null);
        e.setAttribute(ExifInterface.TAG_LIGHT_SOURCE ,null);
        e.setAttribute(ExifInterface.TAG_MAKE ,null);
        e.setAttribute(ExifInterface.TAG_MAKER_NOTE ,null);
        e.setAttribute(ExifInterface.TAG_MAX_APERTURE_VALUE ,null);
        e.setAttribute(ExifInterface.TAG_METERING_MODE ,null);
        e.setAttribute(ExifInterface.TAG_MODEL ,null);
        e.setAttribute(ExifInterface.TAG_OECF ,null);
        e.setAttribute(ExifInterface.TAG_PRIMARY_CHROMATICITIES ,null);
        e.setAttribute(ExifInterface.TAG_REFERENCE_BLACK_WHITE ,null);
        e.setAttribute(ExifInterface.TAG_RELATED_SOUND_FILE ,null);
        e.setAttribute(ExifInterface.TAG_RESOLUTION_UNIT ,null);
        e.setAttribute(ExifInterface.TAG_ROWS_PER_STRIP ,null);
        e.setAttribute(ExifInterface.TAG_SAMPLES_PER_PIXEL ,null);
        e.setAttribute(ExifInterface.TAG_SATURATION ,null);
        e.setAttribute(ExifInterface.TAG_SCENE_CAPTURE_TYPE ,null);
        e.setAttribute(ExifInterface.TAG_SCENE_TYPE ,null);
        e.setAttribute(ExifInterface.TAG_SENSING_METHOD ,null);
        e.setAttribute(ExifInterface.TAG_SHARPNESS ,null);
        e.setAttribute(ExifInterface.TAG_SHUTTER_SPEED_VALUE ,null);
        e.setAttribute(ExifInterface.TAG_SOFTWARE ,null);
        e.setAttribute(ExifInterface.TAG_SPATIAL_FREQUENCY_RESPONSE ,null);
        e.setAttribute(ExifInterface.TAG_SPECTRAL_SENSITIVITY ,null);
        e.setAttribute(ExifInterface.TAG_STRIP_BYTE_COUNTS ,null);
        e.setAttribute(ExifInterface.TAG_STRIP_OFFSETS ,null);
        e.setAttribute(ExifInterface.TAG_SUBJECT_AREA ,null);
        e.setAttribute(ExifInterface.TAG_SUBJECT_DISTANCE ,null);
        e.setAttribute(ExifInterface.TAG_SUBJECT_DISTANCE_RANGE ,null);
        e.setAttribute(ExifInterface.TAG_SUBJECT_LOCATION ,null);
        e.setAttribute(ExifInterface.TAG_SUBSEC_TIME ,null);
        e.setAttribute(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED ,null);
        e.setAttribute(ExifInterface.TAG_SUBSEC_TIME ,null);
        e.setAttribute(ExifInterface.TAG_SUBSEC_TIME_DIGITIZED ,null);
        e.setAttribute(ExifInterface.TAG_SUBSEC_TIME_ORIGINAL ,null);
        e.setAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_LENGTH ,null);
        e.setAttribute(ExifInterface.TAG_THUMBNAIL_IMAGE_WIDTH ,null);
        e.setAttribute(ExifInterface.TAG_TRANSFER_FUNCTION ,null);
        e.setAttribute(ExifInterface.TAG_USER_COMMENT ,null);
        e.setAttribute(ExifInterface.TAG_WHITE_BALANCE ,null);
        e.setAttribute(ExifInterface.TAG_WHITE_POINT ,null);
        e.setAttribute(ExifInterface.TAG_X_RESOLUTION ,null);
        e.setAttribute(ExifInterface.TAG_Y_RESOLUTION ,null);
        e.setAttribute(ExifInterface.TAG_Y_CB_CR_COEFFICIENTS ,null);
        e.setAttribute(ExifInterface.TAG_Y_CB_CR_POSITIONING ,null);
        e.setAttribute(ExifInterface.TAG_Y_CB_CR_SUB_SAMPLING ,null);

        try {
            e.saveAttributes();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Log.d("Apertura2: ", ExifInterface.TAG_APERTURE_VALUE);
    }
*/


}