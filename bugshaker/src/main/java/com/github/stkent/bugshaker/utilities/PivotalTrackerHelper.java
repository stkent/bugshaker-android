package com.github.stkent.bugshaker.utilities;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;

/**
 * Created by slmyldz on 19.09.2016.
 */
public class PivotalTrackerHelper {


    static PivotalTrackerHelper helper;


    Listener listener;
    String project_id;
    String token;
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void setProject_id(String project_id) {
        this.project_id = project_id;
    }

    public void setToken(String token) {
        this.token = token;
        client.addHeader("X-TrackerToken", token);
        client.setLoggingEnabled(true);

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    AsyncHttpClient client = new AsyncHttpClient();

    public static PivotalTrackerHelper install(String project_id,Context context,String token,Listener listener){
        if(helper == null){
            helper = new PivotalTrackerHelper();
            helper.setContext(context);
            helper.setToken(token);
            helper.setProject_id(project_id);
            helper.setListener(listener);
        }
        return helper;
    }

    public static PivotalTrackerHelper getInstance(){
        if(helper!=null){
            return helper;
        }else{
            throw new RuntimeException("You must call install first");
        }

    }

    public void uploadImage(final String name, @NonNull String path) {


        listener.onStartSending();

        final File file = new File(path);
        if (file.isFile()) {
            RequestParams params = new RequestParams();
            try {
                params.put("file", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String url = "https://www.pivotaltracker.com/services/v5/projects/[PROJECT_ID]/uploads".replace("[PROJECT_ID]", project_id);

            client.post(context, url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                    super.onPreProcessResponse(instance, response);

                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, @NonNull byte[] responseBody) {

                        postStory(name,new String(responseBody));
                        //postComment(project_id);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(context, "Lütfen bir daha deneyiniz.", Toast.LENGTH_SHORT).show();

                }

            });
        }
    }




    public void postStory(String name,final String file_attachment) {
        String story_type = "bug";

        String url = "https://www.pivotaltracker.com/services/v5/projects/$PROJECT_ID/stories".replace("$PROJECT_ID", project_id);
        RequestParams params = new RequestParams();
        params.add("story_type", story_type);
        params.add("name", name);


        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                //Toast.makeText(context, "Hata bildiriminiz başarıyla eklendi", Toast.LENGTH_SHORT).show();
                try {
                    postComment(file_attachment,response.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                //Toast.makeText(context, "Hata bildiriminiz başarıyla eklenemedi", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Lütfen bir daha deneyiniz.", Toast.LENGTH_SHORT).show();

            }
        });

    }


    public void postComment(String file_attachment,String story_id){
        String url = "https://www.pivotaltracker.com/services/v5/projects/$PROJECT_ID/stories/$STORY_ID/comments".replace("$PROJECT_ID", project_id).replace("$STORY_ID",story_id);
        RequestParams params = new RequestParams();

        StringEntity entity = null;
        try {
            entity = new StringEntity("{\"file_attachments\":["+file_attachment+"]}");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.post(context, url, entity, "application/json",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                Log.i("success",s);
                listener.onComplete();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String s = new String(responseBody);
                Log.i("error",s);
            }
        });
    }

    public interface Listener{
        void onStartSending();
        void onComplete();
        void onFail(Exception e);
    }

}
