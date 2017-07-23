package example.com.retrofitsample;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity implements Callback<List<GitHubRepo>>, View.OnClickListener {

    private GitHubClient client;

    private EditText editTextUserName;
    private ListView reposListView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client = retrofit.create(GitHubClient.class);
    }

    private void initializeViews() {
        findViewById(R.id.authenticate_button).setOnClickListener(this);
        editTextUserName = (EditText) findViewById(R.id.github_username);
        reposListView = (ListView) findViewById(R.id.repos_list_view);
    }

    public void onClick(View view) {
        String userName = editTextUserName.getText().toString();
        if (!userName.isEmpty()) {
            Call<List<GitHubRepo>> call =
                    client.reposForUser(userName);
            call.enqueue(this);
        } else {
            Toast.makeText(this, "Please enter user name", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(@NonNull Call<List<GitHubRepo>> call, @NonNull Response<List<GitHubRepo>> response) {
        List<GitHubRepo> repos = response.body();
        if (repos != null) {
            System.out.println("Success : " + repos.toString());
            reposListView.setAdapter(
                    new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, repos));
        }
    }

    @Override
    public void onFailure(@NonNull Call<List<GitHubRepo>> call, @NonNull Throwable t) {
        t.printStackTrace();
    }
}

