package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kpirwani on 2/12/16.
 */
public class NYTimesSearchProvider {
    private final Context mContext;
    private final static String API_KEY = "d2688481be33d5847d035860711c7b4e:13:74380972";
    private final static String BASE_URL = "http://api.nytimes.com/svc/search/v2/articlesearch.json?";

    private final static String TOKEN_SEARCH = "q=";
    private final static String TOKEN_PAGE = "&page=";
    private final static String TOKEN_SORT = "&sort=";
    private final static String TOKEN_BEGIN_DATE = "&begin_date=";
    private final static String TOKEN_FILTER_QUERY_NEWS_DESKS = "&fq=news_desk:";

    private final static String TOKEN_API_KEY = "&api-key=";

    private NYTimesSearchQuery mSearchQuery;

    public final static String TAG = "NYTimesSearchProvider";

    private final OkHttpClient client = new OkHttpClient();

    public interface ISearchCallback {
        public void onSearchResponse(NYTimesSearchModel articles, String error);
    }

    public class NYTimesInvalidSearchQueryException extends Exception {
        public NYTimesInvalidSearchQueryException(String message) {
            super(message);
        }
    }

    public static class NYTimesSearchQuery {

        String name;
        int page;
        NYTimesSearchFilter filter;

        public NYTimesSearchQuery(String name, int page, NYTimesSearchFilter filter) {
            this.name = name;
            this.page = page;
            this.filter = filter;
        }

        public String getName() {
            return name;
        }

        public int getPage() {
            return page;
        }

        public NYTimesSearchFilter getSearchFilter() {
            return filter;
        }

        public void setPage(int page) {
            this.page = page;
        }

        @Override
        public String toString() {
            StringBuilder preQueryString = new StringBuilder(BASE_URL);

            if (getName().length() > 0) {
                preQueryString.append(TOKEN_SEARCH + getName());
            }

            if (getPage() != -1) {
                preQueryString.append(TOKEN_PAGE + getPage());
            }

            NYTimesSearchFilter filter = getSearchFilter();

            if (filter.getSortOrder() != null) {
                preQueryString.append(TOKEN_SORT + filter.getSortOrder().getName());
            }

            String beginDate = filter.getBeginDateAsQueryString();
            if (beginDate.length() > 0 && beginDate.equalsIgnoreCase("") == false) {
                preQueryString.append(TOKEN_BEGIN_DATE + beginDate);

            }

            String newsDesks = filter.getNewsDesksAsQueryString();
            if (newsDesks.length() > 0 && newsDesks.equalsIgnoreCase("") == false) {
                preQueryString.append(TOKEN_FILTER_QUERY_NEWS_DESKS + newsDesks);
            }

            if (TOKEN_API_KEY.length() > 0 && API_KEY.length() > 0) {
                preQueryString.append(TOKEN_API_KEY + API_KEY);
            }

            return preQueryString.toString();
        }
    }

    public NYTimesSearchProvider(Context context) {
        mContext = context;
    }

    private NYTimesSearchQuery getSearchQuery() {
        return mSearchQuery;
    }

    public void setSearchQuery(NYTimesSearchQuery searchQuery) {
        mSearchQuery = searchQuery;
    }

    public String validateAndGetFormedUrl() throws NYTimesInvalidSearchQueryException {
        final NYTimesSearchQuery searchQuery = getSearchQuery();
        if (searchQuery == null) {
            throw new NYTimesInvalidSearchQueryException("The search query must be set before searching");
        }

        if (searchQuery.getName().length() == 0) {
            throw new NYTimesInvalidSearchQueryException("The search query must have a token for the name of the article");
        }

        return searchQuery.toString();
    }

    public void search(final ISearchCallback callback) throws NYTimesInvalidSearchQueryException {
        if (isNetworkAvailable() == false) {
            callback.onSearchResponse(null, "Must be connected to the internet to search the NYTimes API");
            return;
        }

        String formedUrl = validateAndGetFormedUrl();

        Request request = new Request.Builder().url(formedUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "Failed search query with exception:\n" + e.getMessage() + "\n request:\n" + getSearchQuery().toString());
                callback.onSearchResponse(null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.i(TAG, "Successful search query with params:\n" + getSearchQuery());
                String errorMessage = null;
                NYTimesSearchModel articles = null;
                try {
                    articles = NYTimesSearchParser.handleResponse(response);
                } catch (IOException e) {
                    errorMessage = e.getMessage();
                }
                callback.onSearchResponse(articles, errorMessage);
            }
        });
    }

    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}
