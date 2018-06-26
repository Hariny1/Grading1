package com.example.harinyg.grading1;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pen.Spen;
import com.samsung.android.sdk.pen.SpenSettingEraserInfo;
import com.samsung.android.sdk.pen.SpenSettingPenInfo;
import com.samsung.android.sdk.pen.document.SpenNoteDoc;
import com.samsung.android.sdk.pen.document.SpenPageDoc;
import com.samsung.android.sdk.pen.engine.SpenColorPickerListener;
import com.samsung.android.sdk.pen.engine.SpenSurfaceView;
import com.samsung.android.sdk.pen.engine.SpenTouchListener;
import com.samsung.android.sdk.pen.settingui.SpenSettingEraserLayout;
import com.samsung.android.sdk.pen.settingui.SpenSettingPenLayout;
import com.samsung.spenemulatorlibrary.ActivityWithSPenLayer;
import samsung.android.sdk.pen.pg.tool.SDKUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.in;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActivityWithSPenLayer implements View.OnClickListener {
    private final int REQUEST_CODE_SELECT_IMAGE_BACKGROUND = 100;
    public static final int SDK_VERSION = Build.VERSION.SDK_INT;
    private static final int PEMISSION_REQUEST_CODE = 1;
    private Context mContext;
    private SpenNoteDoc mSpenNoteDoc;
    private SpenPageDoc mSpenPageDoc;
    private SpenSurfaceView mSpenSurfaceView;
    private SpenSettingPenLayout mPenSettingView;
    private SpenSettingEraserLayout mEraserSettingView;
    private String IMAGE_URL;
    private ImageView mPenBtn;
    private ImageView mEraserBtn;
    //private ImageView mUndoBtn;
    //private ImageView mRedoBtn;
    private ImageView mBgImgBtn;
    private ImageView image;
   // private ImageView mCaptureBtn;
    static Bitmap b = null;
    InputStream in;
    String temp;
    Uri temp1;
    TextView t;
    int index = 0;
    Button next;
    Button prev;
    Button reset;
    Button save;

    private int mToolType = SpenSurfaceView.TOOL_SPEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        // Initialize Spen
        boolean isSpenFeatureEnabled = false;
        Spen spenPackage = new Spen();
        try {
            spenPackage.initialize(this);
            isSpenFeatureEnabled = spenPackage.isFeatureEnabled(Spen.DEVICE_PEN);
        } catch (SsdkUnsupportedException e) {
            if (SDKUtils.processUnsupportedException(this, e) == true) {
                return;
            }
        } catch (Exception e1) {
            Toast.makeText(mContext, "Cannot initialize Spen.", Toast.LENGTH_SHORT).show();
            e1.printStackTrace();
            finish();
        }

        FrameLayout spenViewContainer = (FrameLayout) findViewById(R.id.spenViewContainer);
        RelativeLayout spenViewLayout = (RelativeLayout) findViewById(R.id.spenViewLayout);
        next = (Button) findViewById(R.id.NextButton);
        prev = (Button) findViewById(R.id.PrevButton);
        reset = (Button) findViewById(R.id.ResetButton);
        save = (Button) findViewById(R.id.SaveButton);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        save.setOnClickListener(this);
        reset.setOnClickListener(this);
        // Create PenSettingView
        mPenSettingView = new SpenSettingPenLayout(getApplicationContext(), "", spenViewLayout);
        // Create EraserSettingView
        mEraserSettingView = new SpenSettingEraserLayout(getApplicationContext(), "", spenViewLayout);

        spenViewContainer.addView(mPenSettingView);
        spenViewContainer.addView(mEraserSettingView);
        // Create SpenSurfaceView
        mSpenSurfaceView = new SpenSurfaceView(mContext);
        if (mSpenSurfaceView == null) {
            Toast.makeText(mContext, "Cannot create new SpenSurfaceView.", Toast.LENGTH_SHORT).show();
            finish();
        }
        mSpenSurfaceView.setToolTipEnabled(true);
        spenViewLayout.addView(mSpenSurfaceView);
        mPenSettingView.setCanvasView(mSpenSurfaceView);
        mEraserSettingView.setCanvasView(mSpenSurfaceView);

        // Get the dimension of the device screen.
        Display display = getWindowManager().getDefaultDisplay();
        Rect rect = new Rect();
        display.getRectSize(rect);
        // Create SpenNoteDoc
        try
        {
            mSpenNoteDoc = new SpenNoteDoc(mContext, rect.width(), rect.height());
        }
        catch (IOException e)
        {
            Toast.makeText(mContext, "Cannot create new NoteDoc", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            finish();
        }
        // Add a Page to NoteDoc, get an instance, and set it to the member variable.
        mSpenPageDoc = mSpenNoteDoc.appendPage();
        mSpenPageDoc.setBackgroundColor(Color.WHITE);
        mSpenPageDoc.clearHistory();
        // Set PageDoc to View
        mSpenSurfaceView.setPageDoc(mSpenPageDoc, true);

        initSettingInfo();
        // Register the listener
        mSpenSurfaceView.setColorPickerListener(mColorPickerListener);
        mSpenSurfaceView.setPreTouchListener(onPreTouchSurfaceViewListener);
        mSpenPageDoc.setHistoryListener(mHistoryListener);
        mEraserSettingView.setEraserListener(mEraserListener);
        // t = (TextView) findViewById(R.id.textsample);
        // Set a button
        mPenBtn = (ImageView) findViewById(R.id.penBtn);
        mPenBtn.setOnClickListener(mPenBtnClickListener);

        mEraserBtn = (ImageView) findViewById(R.id.eraserBtn);
        mEraserBtn.setOnClickListener(mEraserBtnClickListener);

        /*mUndoBtn = (ImageView) findViewById(R.id.undoBtn);
        mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
        mUndoBtn.setEnabled(mSpenPageDoc.isUndoable());

        mRedoBtn = (ImageView) findViewById(R.id.redoBtn);
        mRedoBtn.setOnClickListener(undoNredoBtnClickListener);
        mRedoBtn.setEnabled(mSpenPageDoc.isRedoable());*/

        mBgImgBtn = (ImageView) findViewById(R.id.bgImgBtn);
        mBgImgBtn.setOnClickListener(mBgImgBtnClickListener);


        //mCaptureBtn = (ImageView) findViewById(R.id.captureBtn);
        // mCaptureBtn.setOnClickListener(mCaptureBtnClickListener);


        selectButton(mPenBtn);

        mSpenPageDoc.startRecord();

        if (isSpenFeatureEnabled == false) {
            mToolType = SpenSurfaceView.TOOL_FINGER;
            Toast.makeText(mContext, "Device does not support Spen. \n You can draw stroke by finger.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mToolType = SpenSurfaceView.TOOL_SPEN;
        }
        mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);

    }

    private void initSettingInfo() {
        // Initialize Pen settings
        SpenSettingPenInfo penInfo = new SpenSettingPenInfo();
        penInfo.color = Color.RED;
        penInfo.size = 10;
        mSpenSurfaceView.setPenSettingInfo(penInfo);
        mPenSettingView.setInfo(penInfo);

        // Initialize Eraser settings
        SpenSettingEraserInfo eraserInfo = new SpenSettingEraserInfo();
        eraserInfo.size = 40;
        mSpenSurfaceView.setEraserSettingInfo(eraserInfo);
        mEraserSettingView.setInfo(eraserInfo);

    }

    private SpenTouchListener onPreTouchSurfaceViewListener = new SpenTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    enableButton(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    enableButton(true);
                    break;
            }
            return false;
        }
    };

    private final View.OnClickListener mPenBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // When Spen is in stroke (pen) mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_STROKE) {
                // If PenSettingView is open, close it.
                if (mPenSettingView.isShown())
                {
                    mPenSettingView.setVisibility(View.GONE);
                    // If PenSettingView is not open, open it.
                }
                else
                {
                    mPenSettingView.setViewMode(SpenSettingPenLayout.VIEW_MODE_NORMAL);
                    mPenSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in stroke (pen) mode, change it to stroke mode.
            }
            else
            {
                int curAction = mSpenSurfaceView.getToolTypeAction(SpenSurfaceView.TOOL_FINGER);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_STROKE);
                int newAction = mSpenSurfaceView.getToolTypeAction(SpenSurfaceView.TOOL_FINGER);
                if (mToolType == SpenSurfaceView.TOOL_FINGER)
                {
                    if (curAction != newAction)
                    {
                        selectButton(mPenBtn);
                    }
                }
                else
                {
                    selectButton(mPenBtn);
                }
            }

        }
    };


    private final View.OnClickListener mEraserBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            // When Spen is in eraser mode
            if (mSpenSurfaceView.getToolTypeAction(mToolType) == SpenSurfaceView.ACTION_ERASER) {
                // If EraserSettingView is open, close it.
                if (mEraserSettingView.isShown())
                {
                    mEraserSettingView.setVisibility(View.GONE);
                    // If EraserSettingView is not open, open it.
                }
                else
                {
                    mEraserSettingView.setVisibility(View.VISIBLE);
                }
                // If Spen is not in eraser mode, change it to eraser mode.
            }
            else
            {
                selectButton(mEraserBtn);
                mSpenSurfaceView.setToolTypeAction(mToolType, SpenSurfaceView.ACTION_ERASER);
            }
        }
    };

    private final View.OnClickListener mBgImgBtnClickListener = new View.OnClickListener()
    {
        /* @Override
        public void onClick(View v) {
           if(checkPermission()){
                return;
            }
            closeSettingView();
            mSpenSurfaceView.cancelStroke();
            callGalleryForInputImage(REQUEST_CODE_SELECT_IMAGE_BACKGROUND);*/
        @Override
        public void onClick(View v)
        {
                /*
                String imagePath = SDKUtils.getRealPathFromURI(mContext,Uri.parse(IMAGE_URL) );
                t.setText(IMAGE_URL);
                new DownloadImage(mSpenPageDoc).execute(IMAGE_URL);
                */
            mSpenPageDoc.removeAllObject();
            String s = "/storage/emulated/0/Download/a_2_test.jpg";
            //t.setText(s);
            mSpenPageDoc.setBackgroundImage(s);
            mSpenSurfaceView.update();

            /*try {
                in = new java.net.URL(IMAGE_URL).openStream();
                b = BitmapFactory.decodeStream(in);
                ByteArrayOutputStream baos=new  ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG,100, baos);
                byte [] b1=baos.toByteArray();
                temp= Base64.encodeToString(b1, Base64.DEFAULT);
                //temp1=Uri.parse(temp);
                t.setText(temp);
                mSpenPageDoc.setBackgroundImage(temp);
                mSpenSurfaceView.update();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }*/


        }

    };

   /* private final View.OnClickListener mCaptureBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkPermission()) {
                return;
            }
            closeSettingView();
            captureSpenSurfaceView();
        }
    };*/


    private final SpenColorPickerListener mColorPickerListener = new SpenColorPickerListener()
    {
        @Override
        public void onChanged(int color, int x, int y)
        {
            // Set the color from the Color Picker to the setting view.
            if (mPenSettingView != null)
            {
                SpenSettingPenInfo penInfo = mPenSettingView.getInfo();
                penInfo.color = color;
                mPenSettingView.setInfo(penInfo);
            }
        }
    };

    private final SpenSettingEraserLayout.EventListener mEraserListener = new SpenSettingEraserLayout.EventListener()
    {
        @Override
        public void onClearAll()
        {
            // ClearAll button action routines of EraserSettingView
            mSpenPageDoc.removeAllObject();
            mSpenSurfaceView.update();
        }
    };

    private final SpenPageDoc.HistoryListener mHistoryListener = new SpenPageDoc.HistoryListener() {
        @Override
        public void onCommit(SpenPageDoc page)
        {
        }

        @Override
        public void onUndoable(SpenPageDoc page, boolean undoable)
        {
            // Enable or disable the button according to the availability of undo.
            // mUndoBtn.setEnabled(undoable);
        }

        @Override
        public void onRedoable(SpenPageDoc page, boolean redoable)
        {
            // Enable or disable the button according to the availability of redo.
            // mRedoBtn.setEnabled(redoable);
        }
    };


    private void enableButton(boolean isEnable)
    {
        mPenBtn.setEnabled(isEnable);
        mEraserBtn.setEnabled(isEnable);
        mBgImgBtn.setEnabled(isEnable);
        //mPlayBtn.setEnabled(isEnable);
       // mCaptureBtn.setEnabled(isEnable);
        //mUndoBtn.setEnabled(isEnable);
        //mRedoBtn.setEnabled(isEnable);

    }

    private void selectButton(View v)
    {
        // Enable or disable the button according to the current mode.
        mPenBtn.setSelected(false);
        mEraserBtn.setSelected(false);
        v.setSelected(true);
        closeSettingView();
    }

    private void closeSettingView()
    {
        // Close all the setting views.
        mEraserSettingView.setVisibility(SpenSurfaceView.GONE);
        mPenSettingView.setVisibility(SpenSurfaceView.GONE);
    }

    private void setBtnEnabled(boolean clickable)
    {
        // Enable or disable all the buttons.
        mPenBtn.setEnabled(clickable);
        mEraserBtn.setEnabled(clickable);
        //mUndoBtn.setEnabled(clickable);
        //mRedoBtn.setEnabled(clickable);
        mBgImgBtn.setEnabled(clickable);
        //mPlayBtn.setEnabled(clickable);
        //mCaptureBtn.setEnabled(clickable);
    }

    private void captureSpenSurfaceView()
    {
        // Set save directory for a captured image.
        //mSpenPageDoc.setBackgroundColor(Color.WHITE);
        //mSpenPageDoc.getCurrentLayerId();
        //mSpenPageDoc.removeLayer(mSpenPageDoc.getCurrentLayerId());
        //mSpenNoteDoc.removePage(1);
        index++;
        mSpenPageDoc.setBackgroundImage(null);
        mSpenSurfaceView.update();
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SPen/images";
        File fileCacheItem = new File(filePath);
        if (!fileCacheItem.exists())
        {
            if (!fileCacheItem.mkdirs())
            {
                Toast.makeText(mContext, "Save Path Creation Error", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        filePath = fileCacheItem.getPath() + "/CaptureImg" + index + ".png";
        // Capture an image and save it as bitmap.
        Bitmap imgBitmap = mSpenSurfaceView.captureCurrentView(true);
        // Save this bitmap in the server.
        // Create a JSON Object and add this bitmap to it and store in server.
        //Create a JSON Object and add this bitmap as the value to the corresponding name and store in server.
        OutputStream out = null;
        try
        {
            // Save a captured bitmap image to the directory.
            out = new FileOutputStream(filePath);
            imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(mContext, "Captured images were stored in the file \'CaptureImg.png\'.", Toast.LENGTH_SHORT)
                    .show();
        }
        catch (Exception e)
        {
            Toast.makeText(mContext, "Capture failed.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                scanImage(filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        //imgBitmap.recycle();
    }

    private void scanImage(final String imageFileName)
    {
        MediaScannerConnection.scanFile(mContext, new String[]{imageFileName}, null, new MediaScannerConnection.OnScanCompletedListener() {
            @Override
            public void onScanCompleted(String path, Uri uri)
            { }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mSpenNoteDoc != null && mSpenPageDoc.isRecording()) {
            mSpenPageDoc.stopRecord();
        }

        if (mPenSettingView != null) {
            mPenSettingView.close();
        }
        if (mEraserSettingView != null) {
            mEraserSettingView.close();
        }
        if (mSpenSurfaceView != null) {
            if (mSpenSurfaceView.getReplayState() == SpenSurfaceView.REPLAY_STATE_PLAYING) {
                mSpenSurfaceView.stopReplay();
            }
            mSpenSurfaceView.close();
            mSpenSurfaceView = null;
        }

        if (mSpenNoteDoc != null) {
            try {
                mSpenNoteDoc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSpenNoteDoc = null;
        }
    }

    ;

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkPermission() {
        if (SDK_VERSION < 23) {
            return false;
        }
        List<String> permissionList = new ArrayList<String>(Arrays.asList(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE));
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionList.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionList.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionList.size() > 0) {
            requestPermissions(permissionList.toArray(new String[permissionList.size()]), PEMISSION_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PEMISSION_REQUEST_CODE) {
            if (grantResults != null) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(mContext, "permission: " + permissions[i] + " is denied", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(next))
        {
            mSpenPageDoc.removeAllObject();
            String s = "/storage/emulated/0/Download/a_4.jpg";
            // t.setText(s);
            mSpenPageDoc.setBackgroundImage(s);
            mSpenSurfaceView.update();
            // Redo button is clicked
        }
        else if (v.equals(prev))
        {
            mSpenPageDoc.removeAllObject();
            String s = "/storage/emulated/0/Download/a_1.jpg";
            //t.setText(s);
            mSpenPageDoc.setBackgroundImage(s);
            mSpenSurfaceView.update();
        }
        else if (v.equals(save))
        {
            if (checkPermission())
            {
                return;
            }
            closeSettingView();
            captureSpenSurfaceView();
        }
        else if (v.equals(reset))
        {
            mSpenPageDoc.removeAllObject();
            String s = "/storage/emulated/0/Download/a_2_test.jpg";
            //t.setText(s);
            mSpenPageDoc.setBackgroundImage(s);
            mSpenSurfaceView.update();
        }
    }
}

