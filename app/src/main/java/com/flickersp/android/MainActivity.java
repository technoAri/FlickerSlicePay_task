package com.flickersp.android;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;
import android.support.v7.widget.SearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rapidd08 on 1/13/2018.
 */

public class MainActivity extends AppCompatActivity {

    ArrayList<ImageModel> images;
    DataAdapter adapter;
    boolean isFirstData = true;
    RecyclerView recyclerView;
    String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        images = new ArrayList<>();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        if (InternetConnection.checkConnection(getApplicationContext()))
            new GetDataTask().execute();
        else
            Toast.makeText(getApplicationContext(), "Could not load! Check your internet connection and try again.", Toast.LENGTH_SHORT).show();
        adapter = new DataAdapter(getApplicationContext(), images, recyclerView);
        recyclerView.setAdapter(adapter);

        //set load more listener for the RecyclerView adapter
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (images.size() <= 5) {
                    images.add(null);
                    adapter.notifyItemInserted(images.size() - 1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            images.remove(images.size() - 1);
                            adapter.notifyItemRemoved(images.size());

                            //Generating more data
                            int index = images.size();
                            int end = index + 30;
//                            for (int i = index; i < end; i++) {
//                                ImageModel model = new ImageModel();
//                                model.setimages_name("Name");
//                                model.setAndroid_image_url("https://i.stack.imgur.com/g1gyA.png");
//                                images.add(model);
//                            }
                            adapter.notifyDataSetChanged();
                            adapter.setLoaded();
                        }
                    }, 5000);
                } else {
                    //Toast.makeText(MainActivity.this, "Loading data completed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInitialData() {
        isFirstData = false;
        new GetDataTask().execute();
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /**
             * Progress Dialog for User Interaction
             */
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setTitle("Please Wait...");
            dialog.setMessage("Loading images");
            dialog.show();
        }

        @Nullable
        @Override
        protected Void doInBackground(Void... params) {

            JSONObject jsonObject = DataParser.getDataFromWeb();

            try {
                if (jsonObject != null) {
                    JSONObject phots = null;
                    try {
                        phots = jsonObject.getJSONObject("photos");
                        JSONArray array = null;
                        try {
                            array = phots.getJSONArray("photo");
                            int lenArray = array.length();
                            if (lenArray > 0) {

                                if (isFirstData == true) {
                                    for (int jIndex = 0; jIndex < lenArray; jIndex++) {

                                        ImageModel model = new ImageModel();
                                        JSONObject innerObject = array.getJSONObject(jIndex);
                                        String name = innerObject.getString("title");
                                        String farmId = innerObject.getString("farm");
                                        String serverId = innerObject.getString("server");
                                        String id = innerObject.getString("id");
                                        String secret = innerObject.getString("secret");
                                        String imageUrl = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg";

                                        model.setImageName(name);
                                        model.setImageUrl(imageUrl);
                                        images.add(model);
                                    }
                                } else {
                                    for (int jIndex = 0; jIndex < 30; jIndex++) {

                                        ImageModel model = new ImageModel();
                                        JSONObject innerObject = array.getJSONObject(jIndex);
                                        String name = innerObject.getString("title");
                                        String farmId = innerObject.getString("farm");
                                        String serverId = innerObject.getString("server");
                                        String id = innerObject.getString("id");
                                        String secret = innerObject.getString("secret");
                                        //String message = innerObject.getString(Keys.KEY_MESSEGE);
                                        String imageUrl = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + secret + ".jpg";

                                        model.setImageName(name);
                                        model.setImageUrl(imageUrl);
                                        images.add(model);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            if (images.size() > 0) {
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Data Not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null)
                    adapter.getFilter().filter(query);
                searchText = query;
                adapter.notifyDataSetChanged();
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (adapter != null)
                    adapter.getFilter().filter(newText);
                searchText = newText;
                adapter.notifyDataSetChanged();
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
