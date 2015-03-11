package com.sbullet.videorenderscript;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.Type;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//For testing
import org.apache.commons.io.*;


public class MainActivity extends ActionBarActivity {

    int width = 176;
    int height = 144;
    private RenderScript rs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void convertFile(View view)
    {
        InputStream yuy2File = null;
        byte[] bufferedYuy2File = null;
        ImageView iv = (ImageView) findViewById(R.id.imageView); //where to display output

        /*RGB size is larger*/
        byte[] convertedBufferedYuy2File = new byte[width * height * 4];

        try {
            yuy2File = getAssets().open("yuyv.yuv");
            bufferedYuy2File = IOUtils.toByteArray(yuy2File);
        }
        catch(Exception e){
            Log.e("fail", "Failed to open file");
            return;
        }

        rs = RenderScript.create(this);

        //Create type and allocation for YUV
        Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                .setX(width)
                .setY(height);

        Allocation yuvIn = Allocation.createTyped(rs, yuvType.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);

        //Create type and allocation for RGB
        Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(width)
                .setY(height);
        Allocation rgbOut = Allocation.createTyped(rs, rgbaType.create(), Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);


        ScriptC_yuyToRgb yuy2ToRgb = new ScriptC_yuyToRgb(rs, getResources(), R.raw.yuytorgb);
        yuy2ToRgb.set_gIn(yuvIn);
        yuy2ToRgb.set_width(width);
        yuy2ToRgb.set_height(height);

        //Copy data to Allocation, convert to RGB with foreach
        yuvIn.copyFrom(bufferedYuy2File);
        yuy2ToRgb.forEach_root(yuvIn, rgbOut);

        //Copy converted data to byte array
        rgbOut.copyTo(convertedBufferedYuy2File);
        //Show RGB image for testing
        int numPixels = convertedBufferedYuy2File.length / 4; //6 frames
        int pixels[] = new int[numPixels];
        for(int i = 0; i < numPixels; i++){
            int r = convertedBufferedYuy2File[4*i];
            int g = convertedBufferedYuy2File[4*i + 1];
            int b = convertedBufferedYuy2File[4*i + 2];
            int a = convertedBufferedYuy2File[4*i + 3];
            pixels[i] = Color.argb(a, r, g, b);
        }

         Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
         bmp.setPixels(pixels, 0, width, 0, 0, width, height);
         iv.setImageBitmap(bmp);






        //Show YUV image for testing
        /**
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(bufferedYuy2File, ImageFormat.YUY2, width, height, null);
        yuvImage.compressToJpeg(new Rect(0, 0, width, height), 50, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        iv.setImageBitmap(image);**/
    }
}
