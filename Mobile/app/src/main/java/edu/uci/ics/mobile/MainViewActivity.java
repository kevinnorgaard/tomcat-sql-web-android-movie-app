package edu.uci.ics.mobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainViewActivity extends Activity {
    private Context context;
    private EditText fullTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        context = getApplicationContext();
        fullTextSearch = findViewById(R.id.fullTextSearch);
    }

    public void onFullTextSearch(View view) {
        Intent intent = new Intent(view.getContext(), MovieListViewActivity.class);
        intent.putExtra("query", fullTextSearch.getText().toString());
        view.getContext().startActivity(intent);
    }

    public void onAllMoviesBtn(View view) {
        Intent listPage = new Intent(view.getContext(), MovieListViewActivity.class);
        view.getContext().startActivity(listPage);
    }
}