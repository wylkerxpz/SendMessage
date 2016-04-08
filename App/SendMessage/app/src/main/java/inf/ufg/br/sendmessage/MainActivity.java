package inf.ufg.br.sendmessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    UserLocalStore userLocalStore;
    TextView txtMidia;
    EditText assunto, mensagem;

    int isImage = 0;

    MyCustomAdapter dataAdapter = null;

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userLocalStore = new UserLocalStore(this);

        txtMidia = (TextView) findViewById(R.id.txtMidia);

        assunto = (EditText) findViewById(R.id.assunto);
        mensagem = (EditText) findViewById(R.id.mensagem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_sender) {
            StringBuilder receiver = new StringBuilder();

            ArrayList<Disciplina> disciplinaList = dataAdapter.disciplinaList;
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < disciplinaList.size(); i++) {
                Disciplina disciplina = disciplinaList.get(i);
                if (disciplina.isSelected()) {
                    //Adiciona chave primaria a lista
                    list.add(disciplina.getDisciplina_pk());
                }
            }

            for (int i =0; i < list.size(); i++) {
                if(i == list.size()-1) {
                    receiver.append(list.get(i).toString());
                } else {
                    receiver.append(list.get(i).toString());
                    receiver.append(",");
                }
            }

            User user = userLocalStore.getLoggedInUser();
            String subject = assunto.getText().toString();
            String mensage = mensagem.getText().toString();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            if(subject.isEmpty()) {

                builder.setMessage("Preencha o assunto").setTitle("Ops!!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                //Toast.makeText(getApplicationContext(),"Preencha o assunto", Toast.LENGTH_SHORT).show();
            } else if(mensage.isEmpty()) {
                builder.setMessage("Preencha a mensagem").setTitle("Ops!!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                //Toast.makeText(getApplicationContext(),"Preencha a mensagem", Toast.LENGTH_SHORT).show();
            } else if (list.size() == 0) {
                builder.setMessage("Selecione alguma disciplina").setTitle("Ops!!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                //Toast.makeText(getApplicationContext(), "Selecione alguma disciplina", Toast.LENGTH_SHORT).show();
            } else {
                Intent i = new Intent(MainActivity.this, Send.class);
                i.putExtra("isImage", isImage);
                if(isImage > 0) {
                    i.putExtra("filePath", fileUri.getPath());
                }
                i.putExtra("sender", user.usuario_pk + "");
                i.putExtra("receiver", receiver+"");
                i.putExtra("subject", subject);
                i.putExtra("mensage", mensage);
                startActivity(i);
            }
        }

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
            User user = userLocalStore.getLoggedInUser();
            setTitle("Ola " + user.usuario_nome);
            displayListView();
            buttonClick();
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

    private void displayListView() {
        User user = userLocalStore.getLoggedInUser();

        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(user.listaDisciplinas);
            JSONArray jArray = jsonObj.getJSONArray("disciplinas");

            ArrayList<Disciplina> listaDisciplina = new ArrayList<>();

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);
                Disciplina disciplina = new Disciplina(jObj.getInt("disciplina_pk"), jObj.getString("disciplina_nome"), jObj.getBoolean("selected"));
                listaDisciplina.add(disciplina);
            }

            //create an ArrayAdaptar from the String Array
            dataAdapter = new MyCustomAdapter(this, R.layout.disciplinas_info, listaDisciplina);
            ListView listView = (ListView) findViewById(R.id.listView1);
            // Assign adapter to ListView
            listView.setAdapter(dataAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Disciplina> {

        private ArrayList<Disciplina> disciplinaList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Disciplina> disciplinaList) {
            super(context, textViewResourceId, disciplinaList);
            this.disciplinaList = new ArrayList<Disciplina>();
            this.disciplinaList.addAll(disciplinaList);
        }

        private class ViewHolder {
            //TextView disciplina_pk;
            CheckBox disciplina_nome;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.disciplinas_info, null);

                holder = new ViewHolder();
                //holder.disciplina_pk = (TextView) convertView.findViewById(R.id.code);
                holder.disciplina_nome = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.disciplina_nome.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Disciplina disciplina = (Disciplina) cb.getTag();
                        disciplina.setSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Disciplina disciplina = disciplinaList.get(position);
            //holder.disciplina_pk.setText(" (" +  disciplina.getDisciplina_pk() + ")");
            holder.disciplina_nome.setText(disciplina.getDisciplina_nome());
            holder.disciplina_nome.setChecked(disciplina.isSelected());
            holder.disciplina_nome.setTag(disciplina);

            return convertView;
        }

    }

    private void buttonClick() {

        Button btnCaptureMidia = (Button) findViewById(R.id.btnCaptureMidia);
        btnCaptureMidia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final CharSequence[] options = { "Capturar Imagem", "Capturar Video","Cancelar" };

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Adicionar Midia!");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Capturar Imagem")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                        } else if (options[item].equals("Capturar Video")) {
                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                            startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                        } else if (options[item].equals("Cancelar")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // launching upload activity
                txtMidia.setText("Imagem Anexada");
                isImage = 1;
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "O usuario cancelou a captura de imagem", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Desculpe! Falha ao capturar a imagem", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // launching upload activity
                txtMidia.setText("Video Anexado");
                isImage = 2;
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "Usuario cancelou a gravacao do video", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Desculpe Falha ao gravar video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Ops! Falhou ao criar "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}