package net.simplifiedcoding.swachbharat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.simplifiedcoding.swachbharat.adapters.CustomAdapter;
import net.simplifiedcoding.swachbharat.models.DataModel;
import net.simplifiedcoding.swachbharat.models.MyData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dragoon on 18-Jan-17.
 */
public class CheckComplaint extends Activity {
    private RecyclerView recycler;
    private CustomAdapter adapter;
    ArrayList<DataModel> data;
    public static View.OnClickListener myOnClickListener;
    String selectedItemId;
    ProgressDialog p;
    String  json_string0,json_string1,json_string2;
    JSONObject jsonObject,JO;
    JSONArray jsonArray;
    String JSON_String;

    EditText search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_complaint);

        myOnClickListener = new MyOnClickListener(this);

        search = (EditText) findViewById( R.id.search);
        PrefManager pref = new PrefManager(this);
        pref.setEvent("0");
        recycler = (RecyclerView) findViewById(R.id.recycler);
        setupRecycler();
        addTextListener();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recycler.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycler.setLayoutManager(layoutManager);

        // create an empty adapter and add it to the recycler view
        data = new ArrayList<DataModel>();
        for (int i = 0; i < MyData.com_date.size(); i++) {
            data.add(new DataModel(
                    MyData.com_date.get(i),
                    MyData.com_id.get(i),
                    MyData.com_image.get(i),
                    MyData.com_status.get(i)
            ));
        }


        adapter = new CustomAdapter(CheckComplaint.this,data);
        recycler.setAdapter(adapter);

    }

    private class MyOnClickListener implements View.OnClickListener {

        private final Context context;

        private MyOnClickListener(Context context) {
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            updateItem(v);
        }

        private void updateItem(View v) {
            int selectedItemPosition = recycler.getChildPosition(v);
            RecyclerView.ViewHolder viewHolder
                    = recycler.findViewHolderForPosition(selectedItemPosition);
            TextView textViewName
                    = (TextView) viewHolder.itemView.findViewById(R.id.complaint_number);
            String selectedName = (String) textViewName.getText();
             //selectedItemId = -1;

            selectedItemId = selectedName;

            new BackGroundTask().execute();
        }
    }

    class BackGroundTask extends AsyncTask<Void, Void, String> {

        // params,progress,result
        String json_url[]=new String[1];

        @Override
        protected void onPreExecute() {// handled by UI threads
            json_url[0] = Constants.UP_STATUS+"?id=" + selectedItemId;

            p = new ProgressDialog(CheckComplaint.this);
            p.setMessage("Loading...");
            p.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }// can be used for displaying progress bars

        @Override
        protected String doInBackground(Void... voids) {// this carries out the background task
            // StringBuilder stringBuilder = new StringBuilder();
            try {

                StringBuilder stringBuilder = new StringBuilder();
                URL url = new URL(json_url[0]);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));


                while ((JSON_String = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_String + "\n");
                }
                json_string0 = stringBuilder.toString().trim();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();


                return json_string0;    // trim deletes white space
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            json_string0 = result;
            try {

                JO = new JSONObject(json_string0);
              //  jsonArray = JO.optJSONArray("update");

                String check = JO.getString("update");

                if(check.equals("SUCCESS"))
                {
                    Toast.makeText(CheckComplaint.this,"Succesfully Updated", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(CheckComplaint.this,"Updated Failed", Toast.LENGTH_SHORT).show();

                }


                System.out.println(json_string0);


                p.dismiss();

            }
            catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void addTextListener(){

        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence query, int start, int before, int count) {

                query = query.toString().toLowerCase();



                // use a linear layout manager since the cards are vertically scrollable
                final LinearLayoutManager layoutManager = new LinearLayoutManager(CheckComplaint.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recycler.setLayoutManager(layoutManager);

                // create an empty adapter and add it to the recycler view
                ArrayList<DataModel> filteredData = new ArrayList<DataModel>();
                for (int i = 0; i < MyData.com_date.size(); i++) {
                    String text = MyData.com_date.get(i).toLowerCase();

                        if (text.contains(query)) {

                            filteredData.add(new DataModel(
                                    MyData.com_date.get(i),
                                    MyData.com_id.get(i),
                                    MyData.com_image.get(i),
                                    MyData.com_status.get(i)
                            ));
                        }
                        else {
                            text = String.valueOf(MyData.com_id.get(i));
                            if (text.contains(query)) {

                                filteredData.add(new DataModel(
                                        MyData.com_date.get(i),
                                        MyData.com_id.get(i),
                                        MyData.com_image.get(i),
                                        MyData.com_status.get(i)
                                ));
                            }
                        else {
                                int t = MyData.com_status.get(i);
                                if (t == 0) {
                                    text = "pending";
                                } else {
                                    text = "done";
                                }
                                if (text.contains(query)) {

                                    filteredData.add(new DataModel(
                                            MyData.com_date.get(i),
                                            MyData.com_id.get(i),
                                            MyData.com_image.get(i),
                                            MyData.com_status.get(i)
                                    ));
                                }
                            }
                        }
                }


                adapter = new CustomAdapter(CheckComplaint.this,filteredData);
                recycler.setAdapter(adapter);

                /*mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mAdapter = new SimpleAdapter(filteredList, MainActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();  // data set changed*/
            }
        });
    }




}
