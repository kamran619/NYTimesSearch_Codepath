package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

/**
 * Created by kpirwani on 2/14/16.
 */
public enum NYTimesSortOrder {
    NEWEST("newest"),
    OLDEST("oldest");

    String name;

    private NYTimesSortOrder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}