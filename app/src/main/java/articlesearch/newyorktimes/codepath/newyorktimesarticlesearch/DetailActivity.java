package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.wbDetail)
    WebView wbDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        loadWebview();
    }

    private void loadWebview() {
        String url = getIntent().getExtras().getString(ListActivity.INTENT_EXTRA_WEBSITE);
        wbDetail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        wbDetail.loadUrl(url);
    }
}
