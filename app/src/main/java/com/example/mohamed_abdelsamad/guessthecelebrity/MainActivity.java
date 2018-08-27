package com.example.mohamed_abdelsamad.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celebnames = new ArrayList<String>();
    ArrayList<String> celebUrl = new ArrayList<String>();
    int chosenceleb = 0;
    ImageView Mcelebimage;
    int currentloc = 0;
    String[] answer = new String[4];
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;

    public void click(View view) {
        if (view.getTag().toString().equals(Integer.toString(currentloc))) {
            Toast.makeText(MainActivity.this, "Correct !!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(MainActivity.this, "Wrong !!" + celebnames.get(chosenceleb), Toast.LENGTH_SHORT).show();


        addquestion();
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap myBitmap = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                InputStream inputStream = c.getInputStream();
                myBitmap = BitmapFactory.decodeStream(inputStream);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class Task extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                InputStream inputStream = c.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mcelebimage = (ImageView) findViewById(R.id.image);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btn4 = (Button) findViewById(R.id.btn4);

        try {
            Task task = new Task();
            String result = null;
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitresult = result.split("<div class=\"sidebarContainer\">");
            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebUrl.add(m.group(1));
            }
            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitresult[0]);
            while (m.find()) {
                celebnames.add(m.group(1));
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        addquestion();


    }

    public void addquestion() {

        try {
            ImageDownloader imageDownloader = new ImageDownloader();
            Bitmap bitmap;
            bitmap = imageDownloader.execute(celebUrl.get(chosenceleb)).get();
            Mcelebimage.setImageBitmap(bitmap);
            int incorrect;
            Random random = new Random();
            chosenceleb = random.nextInt(celebUrl.size());
            currentloc = random.nextInt(4);
            for (int i = 0; i < 4; i++) {
                if (i == currentloc)
                    answer[i] = celebnames.get(chosenceleb);
                else {
                    incorrect = random.nextInt(celebUrl.size());
                    while (incorrect == chosenceleb) {
                        incorrect = random.nextInt(celebUrl.size());
                    }
                    answer[i] = celebnames.get(incorrect);

                }

            }

            btn1.setText(answer[0]);
            btn2.setText(answer[1]);
            btn3.setText(answer[2]);
            btn4.setText(answer[3]);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
