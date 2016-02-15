package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class
        ListActivity extends AppCompatActivity implements ThumbnailArticleAdapter.IRecyclerViewOnItemClicked, FilterFragment.OnFragmentInteractionListener, FilterFragment.IFilterFragmentCallback{

    @Bind(R.id.rvArticles)
    RecyclerView rvArticles;

    @Bind(R.id.pbFetching)
    ProgressBar pbFetching;

    private NYTimesSearchProvider mSearchProvider;
    private ThumbnailArticleAdapter mAdapter;

    private NYTimesSearchProvider.NYTimesSearchQuery mLastQuery;

    public static final String INTENT_EXTRA_WEBSITE = "website";

    private NYTimesSearchFilter mSearchFilter;

    public NYTimesSearchProvider getmSearchProvider() {
        return mSearchProvider;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        setupSearchFilter();
        setupProvider();
        setupLayout();
        setupAdapter();
    }

    private void setupSearchFilter() {
        mSearchFilter = new NYTimesSearchFilter();
    }

    private void setupProvider() {
        mSearchProvider = new NYTimesSearchProvider(getApplicationContext());
    }

    private void setupLayout() {
        final int numberOfColumns = 4;
        // Attach the layout manager to the recycler view
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(numberOfColumns, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);
        rvArticles.addItemDecoration(new VerticalSpaceItemDecoration(10));
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                NYTimesSearchProvider.NYTimesSearchQuery lastQuery = mLastQuery;
                lastQuery.setPage(page);
                getmSearchProvider().setSearchQuery(lastQuery);
                mLastQuery = lastQuery;
                try {
                    getmSearchProvider().search(new NYTimesSearchProvider.ISearchCallback() {
                        @Override
                        public void onSearchResponse(final NYTimesSearchModel articles, final String error) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (articles == null || error != null) {
                                        Log.d("ListActivity", "Error fetching endless results: " + error);
                                    }
                                    int curSize = mAdapter.getItemCount();
                                    NYTimesSearchModel.Docs[] currentDocs = mAdapter.mArticles.get(0).response.docs;
                                    NYTimesSearchModel.Docs[] newDocs = articles.response.docs;
                                    NYTimesSearchModel.Docs[] mergedDocs = new NYTimesSearchModel.Docs[currentDocs.length + newDocs.length];
                                    System.arraycopy(currentDocs, 0, mergedDocs, 0, currentDocs.length);
                                    System.arraycopy(newDocs, 0, mergedDocs, currentDocs.length, newDocs.length);
                                    mAdapter.mArticles.get(0).response.docs = mergedDocs;
                                    mAdapter.notifyItemRangeInserted(curSize, mergedDocs.length - 1);
                                }
                            });
                        }
                    });
                } catch (NYTimesSearchProvider.NYTimesInvalidSearchQueryException e) {


                }
            }
        });
    }

    private void setupAdapter() {
        final ArrayList<NYTimesSearchModel> list = new ArrayList<NYTimesSearchModel>();
        mAdapter = new ThumbnailArticleAdapter(getApplicationContext(), list, this);
        rvArticles.setAdapter(mAdapter);
    }

    private void search(NYTimesSearchProvider.NYTimesSearchQuery query) {
        mLastQuery = query;
        final NYTimesSearchProvider provider = getmSearchProvider();
        provider.setSearchQuery(query);
        try {
            showRecyclerView(false);
            showLoadingSpinner();
            provider.search(new NYTimesSearchProvider.ISearchCallback() {
                @Override
                public void onSearchResponse(final NYTimesSearchModel articles, final String error) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleResponse(articles, error);
                        }
                    });
                }
            });
        } catch (NYTimesSearchProvider.NYTimesInvalidSearchQueryException e) {
            hideLoadingSpinner();
            showRecyclerView(false);
            Log.d(NYTimesSearchProvider.TAG, e.getMessage());
        }
    }

    private void addDataSource(NYTimesSearchModel articles) {
        mAdapter.mArticles.clear();
        mAdapter.mArticles.add(articles);
        mAdapter.notifyDataSetChanged();
    }

    private void handleResponse(NYTimesSearchModel articles, String error) {
        hideLoadingSpinner();
        if (articles != null && error == null) {
            addDataSource(articles);
            showRecyclerView(true);
        } else {
            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoadingSpinner() {
        pbFetching.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {
        pbFetching.setVisibility(View.GONE);
    }

    private void showRecyclerView(boolean show) {
        int visible = (show == true) ? View.VISIBLE : View.GONE;
        rvArticles.setVisibility(visible);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        MenuItem searchItem = (MenuItem) menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isValidQuery(query) == false) {
                    Toast.makeText(getApplicationContext(), "The search text must be at least 1 letter long", Toast.LENGTH_SHORT).show();
                    return false;
                }
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();
                NYTimesSearchProvider.NYTimesSearchQuery searchQuery = new NYTimesSearchProvider.NYTimesSearchQuery(query, 0, getSearchFilter());
                search(searchQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private boolean isValidQuery(String query) {
        return query != null && query.length() > 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FilterFragment fragment = FilterFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putParcelable("filter", Parcels.wrap(getSearchFilter()));
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(int position) {
        NYTimesSearchModel.Docs doc = mAdapter.mArticles.get(0).response.docs[position];
        Intent intent = new Intent(ListActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_EXTRA_WEBSITE, doc.web_url);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public NYTimesSearchFilter getSearchFilter() {
        return mSearchFilter;
    }

    @Override
    public void onFiltersSaved(NYTimesSearchFilter filter) {
        mSearchFilter = filter;
    }
}
