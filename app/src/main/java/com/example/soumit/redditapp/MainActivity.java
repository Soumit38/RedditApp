package com.example.soumit.redditapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.soumit.redditapp.account.LoginActivity;
import com.example.soumit.redditapp.comments.CommentsActivity;
import com.example.soumit.redditapp.model.Feed;
import com.example.soumit.redditapp.model.entry.Entry;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    URLs urLs = new URLs();

    private Button btnRefreshFeed;
    private EditText mFeedName;
    private String currentFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: started");

        setupToolbar();

        btnRefreshFeed = (Button) findViewById(R.id.btnRefreshFeed);
        mFeedName = (EditText) findViewById(R.id.editTextFeed);

        init();

        btnRefreshFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedName = mFeedName.getText().toString();
                if(!feedName.equals("")){
                    currentFeed = feedName;
                    init();
                }else {
                    init();
                }
            }
        });
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.d(TAG, "onMenuItemClick: clicked : " + menuItem);
                switch (menuItem.getItemId()){
                    case R.id.navLogin:
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                }

                return false;
            }
        });
    }

    public void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(urLs.BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        Call<Feed> call = feedAPI.getFeed(currentFeed);

        call.enqueue(new Callback<Feed>() {
            @Override
            public void onResponse(Call<Feed> call, Response<Feed> response) {
                Log.d(TAG, "onResponse: feed: " + response.body().toString());
                Log.d(TAG, "onResponse: Server Response : " + response.toString());

                List<Entry> entrys = response.body().getEntrys();

                Log.d(TAG, "onResponse: entrys: " + response.body().getEntrys());

//                Log.d(TAG, "onResponse: author: " + entrys.get(0).getAuthor().getName());
//                Log.d(TAG, "onResponse: title : " + entrys.get(0).getTitle());
//                Log.d(TAG, "onResponse: Updated : " + entrys.get(0).getUpdated());

                final ArrayList<Post> posts = new ArrayList<Post>();

                for(int i=0;i<entrys.size();i++){
                    ExtractXML extractXML1 = new ExtractXML("<a href=" , entrys.get(i).getContent());
                    List<String> postContent = extractXML1.start();

                    ExtractXML extractXML2 = new ExtractXML("<img src=" , entrys.get(i).getContent());
                    try{
                        postContent.add(extractXML2.start().get(0));
                    }catch (NullPointerException e){
                        postContent.add(null);
                        Log.e(TAG, "onResponse: NullPointerException(thumbnail)" + e.getMessage() );
                    }catch (IndexOutOfBoundsException e){
                        postContent.add(null);
                        Log.e(TAG, "onResponse: IndexOutofBoundException(thumbnail)" + e.getMessage() );
                    }

                    int lastPosition = postContent.size()-1;

                    try {
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                entrys.get(i).getAuthor().getName(),
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entrys.get(i).getId()
                        ));
                    }catch (NullPointerException e){
                        Log.d(TAG, "onResponse: NullPointerException : " + e.getMessage());
                        posts.add(new Post(
                                entrys.get(i).getTitle(),
                                "None",
                                entrys.get(i).getUpdated(),
                                postContent.get(0),
                                postContent.get(lastPosition),
                                entrys.get(i).getId()
                        ));
                    }


                }

//                for(int j=0;j<posts.size();j++){
//                    Log.d(TAG, "onResponse: \n " +
//                            "PostURL: " + posts.get(j).getPostURL() + "\n" +
//                            "ThumbnailURL: " + posts.get(j).getThumbnailURL() + "\n" +
//                            "Title : " + posts.get(j).getTitle() + "\n" +
//                            "Author : " + posts.get(j).getAuthor() + "\n" +
//                            "Updated : " + posts.get(j).getDate_updated() + "\n" +
//                            "PostID : " + posts.get(j).getId() + "\n"
//                    );
//                }

                ListView listView = (ListView) findViewById(R.id.listView);
                CustomListAdapter customListAdapter =
                        new CustomListAdapter(MainActivity.this, R.layout.card_layout_main, posts);
                listView.setAdapter(customListAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        Log.d(TAG, "onItemClick: Clicked : " + posts.get(position).toString());
                        Intent intent = new Intent(MainActivity.this, CommentsActivity.class);
                        intent.putExtra(getString(R.string.post_url), posts.get(position).getPostURL());
                        intent.putExtra(getString(R.string.thumbnail_url), posts.get(position).getThumbnailURL());
                        intent.putExtra(getString(R.string.post_title), posts.get(position).getTitle());
                        intent.putExtra(getString(R.string.post_author), posts.get(position).getAuthor());
                        intent.putExtra(getString(R.string.post_updated), posts.get(position).getDate_updated());
                        intent.putExtra(getString(R.string.post_id), posts.get(position).getId());

                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<Feed> call, Throwable t) {
                Log.e(TAG, "onFailure: unable to retrieve RSS: "+ t.getMessage() );
                Toast.makeText(MainActivity.this, "An error occured!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

}














