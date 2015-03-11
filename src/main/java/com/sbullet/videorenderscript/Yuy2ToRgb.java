package com.sbullet.videorenderscript;

import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsic;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

/**
 * Created by Chris Schneider on 3/9/2015.
 */
public class Yuy2ToRgb {


    private RenderScript            mRS;
    private ScriptIntrinsicYuvToRGB mYuvToRgbIntrinsic;
    private Allocation              mAllocIn           = null;
    private Allocation              mAllocOut          = null;
    private Type.Builder            mYuvType           = null;
    private Type.Builder            mRgbaType          = null;





    public Yuy2ToRgb(int id, RenderScript rs)
    {

    }


    public static byte[] yuy2ToRgb(byte[] videoBuffer)
    {

        return null;
    }

}
