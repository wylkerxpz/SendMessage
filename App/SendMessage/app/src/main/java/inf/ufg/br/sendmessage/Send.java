package inf.ufg.br.sendmessage;

import inf.ufg.br.sendmessage.AndroidMultiPartEntity.ProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

public class Send extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    UserLocalStore userLocalStore;

    private ProgressBar progressBar;
    private int isImage;
    private String sender;
    private String receiver;
    private String subject;
    private String mensage;

    private String filePath = null;
    private TextView txtPercentage, txtAssunto, txtMensagem;
    private ImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;
    long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        userLocalStore = new UserLocalStore(this);

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        txtAssunto = (TextView) findViewById(R.id.txtAssunto);
        txtMensagem= (TextView) findViewById(R.id.txtMensagem);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);

        // Receiving the data from previous activity
        Intent i = getIntent();

        //Campos de mensagem
        sender = i.getStringExtra("sender"); //remetente
        receiver = i.getStringExtra("receiver"); //destinatarios
        subject = i.getStringExtra("subject");
        txtAssunto.setText("Assunto: " + subject);
        mensage = i.getStringExtra("mensage");
        txtMensagem.setText("Mensagem: " + mensage);

        isImage = i.getIntExtra("isImage",0);

        if(isImage == 0) {
            filePath = null;
        } else {
            filePath = i.getStringExtra("filePath");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.send_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {
            userLocalStore.clearUserData();
            userLocalStore.setUserLoggedIn(false);
            Intent loginIntent = new Intent(this, Login.class);
            startActivity(loginIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (authenticate() == true) {
            //Generate list View from ArrayList
            // Displaying the image or video on the screen
            previewMedia(isImage);

            btnUpload.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // uploading the file to server
                    new UploadFileToServer().execute();
                }
            });
        }
    }

    private boolean authenticate() {
        if (userLocalStore.getLoggedInUser() == null) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            return false;
        }
        return true;
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(int isImage) {
        // Checking whether captured media is image or video
        if (isImage == 1) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        } else if (isImage == 2){
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(filePath);
            // start playing
            vidPreview.start();
        }
    }

    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[0]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[0]) + "%");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.SEND_MENSAGE_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                if(filePath != null) {
                    File sourceFile = new File(filePath);
                    // Adding file data to http body
                    entity.addPart("midia", new FileBody(sourceFile));
                }

                // Parametros da mensagem enviados ao servidor
                entity.addPart("sender", new StringBody(sender));
                entity.addPart("receiver", new StringBody(receiver));
                entity.addPart("subject", new StringBody(subject));
                entity.addPart("mensage", new StringBody(mensage));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Ocorreu um erro! Http codigo de status: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }

    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Resposta do Servidor")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        goMain();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

}