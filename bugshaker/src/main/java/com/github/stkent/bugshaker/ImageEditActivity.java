package com.github.stkent.bugshaker;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.stkent.bugshaker.utilities.DrawView;
import com.github.stkent.bugshaker.utilities.DrawableUtils;
import com.github.stkent.bugshaker.utilities.PivotalTrackerHelper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageEditActivity extends AppCompatActivity implements PivotalTrackerHelper.Listener {


    RelativeLayout root;
    DrawView drawView;

    Button send;
    PivotalTrackerHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        root = (RelativeLayout) findViewById(R.id.root);
        send = (Button) findViewById(R.id.send);
        drawView = new DrawView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.addView(drawView,params);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            drawView.setBackgroundDrawable(DrawableUtils.readDrawableFromUri(this,getIntent().getData()));
        } else {
            drawView.setBackground(DrawableUtils.readDrawableFromUri(this,getIntent().getData()));
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputDialog();
            }
        });

        helper =PivotalTrackerHelper.install(getIntent().getStringExtra("project_id"), ImageEditActivity.this, getIntent().getStringExtra("token"),this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ok,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.ok){
            showInputDialog();
        }
        return super.onOptionsItemSelected(item);
    }



    private void showInputDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Story name:");

        // Use an EditText view to get user input.
        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

                drawView.setDrawingCacheEnabled(true);
                Bitmap b = drawView.getDrawingCache();

                onStartSending();

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(getCacheDir()+"/pivotal_tracker_file.png");
                    b.compress(Bitmap.CompressFormat.PNG, 95, fos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    helper.getInstance().uploadImage(input.getText().toString(),getCacheDir()+"/pivotal_tracker_file.png");

                }



                return;
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        builder.show();
    }


    @Override
    public void onStartSending() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                ProgressDialog dialog = new ProgressDialog(ImageEditActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Sending...");
                dialog.show();
            }
        });

    }

    @Override
    public void onComplete() {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onFail(Exception e) {
        finish();
        Toast.makeText(ImageEditActivity.this, "Error when sending story!", Toast.LENGTH_SHORT).show();
    }

}
