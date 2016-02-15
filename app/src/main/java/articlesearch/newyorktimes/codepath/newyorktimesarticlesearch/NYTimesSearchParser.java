package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;

import okhttp3.Response;

/**
 * Created by kpirwani on 2/12/16.
 */
public class NYTimesSearchParser {
    private static final Gson gson = new Gson();

    public static NYTimesSearchModel handleResponse(Response response) throws IOException {
        Reader reader = response.body().charStream();
        NYTimesSearchModel model = gson.fromJson(reader, NYTimesSearchModel.class);
        return model;
    }
}
