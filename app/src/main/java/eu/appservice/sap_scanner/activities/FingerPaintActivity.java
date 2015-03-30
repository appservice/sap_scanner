package eu.appservice.sap_scanner.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import eu.appservice.sap_scanner.R;
import eu.appservice.sap_scanner.Utils;


@TargetApi(Build.VERSION_CODES.BASE)
public class FingerPaintActivity extends ActionBarActivity// implements ColorPickerDialog.OnColorChangedListener  //Graphics
{
    private String SIGN_DIR_NAME;     //implements ColorPickerDialog.OnColorChangedListener
    private Paint mPaint;
    private Bitmap mBitmap;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        setContentView(new MyView(this));

        SIGN_DIR_NAME = Environment
                .getExternalStorageDirectory()
                + File.separator
                + getApplicationContext().getString(R.string.main_folder)
                + File.separator
                + getApplicationContext().getString(R.string.signatures_folder);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFF0000FF);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);


        mPaint.setStrokeWidth(4);


    }


  /*  public void colorChanged(int color) {
        mPaint.setColor(color);
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //super.onPrepareOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_finger_paint_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //    mPaint.setXfermode(null);
        //   mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case R.id.home:
                finish();
                return true;

            case R.id.menu_finger_paint_clear:

                setContentView(new MyView(this));

                return true;
            case R.id.menu_finger_paint_save:

                String signatureResult = "";

                try {
                    signatureResult = saveToFile();
                } catch (IOException e) {

                    e.printStackTrace();
                }
                Intent result = new Intent();
                result.putExtra("SIGNATURE_RESULT", signatureResult);
                setResult(RESULT_OK, result);
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private String saveToFile() throws IOException {
        String fileName = Utils.reformatDate(Utils.nowDate(), "ddMMyy_HHmmss") + ".png";
        if (Utils.isExternalStorageWritable()) {

            File signaturesFolder = new File(SIGN_DIR_NAME);
            File plik = new File(signaturesFolder, fileName);
            if (!plik.exists())
                if (!plik.createNewFile()) //creating file
                    Log.w("creating file", "can't create file " + fileName);

            FileOutputStream fos = new FileOutputStream(plik, true);

            mBitmap.compress(CompressFormat.PNG, 90, fos);

            fos.flush();
            fos.close();
            return fileName;
        }
        return "";
    }

    @TargetApi(Build.VERSION_CODES.BASE)
    public class MyView extends View {

        private static final float TOUCH_TOLERANCE = 1;
        private Canvas mCanvas;
        private Path mPath;
        private Paint mBitmapPaint;
        private float mX, mY;

        public MyView(Context c) {
            super(c);

        }



        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.BLACK);
            mCanvas = new Canvas(mBitmap);
          /*  */
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);


            mBitmapPaint.setColor(Color.WHITE);
            mCanvas.drawRect(1, 1, mBitmap.getWidth() - 1, mBitmap.getHeight() - 1, mBitmapPaint);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFCCCCCC);
            //  canvas.drawColor(Color.RED);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);


            canvas.drawPath(mPath, mPaint);
        }

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }


}



