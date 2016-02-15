package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import org.parceler.Parcel;

import java.util.EnumSet;

/**
 * Created by kpirwani on 2/14/16.
 */
@Parcel
public class NYTimesSearchFilter {
    public NYTimesSortOrder mSortOrder;
    public EnumSet<NewsDesks> mNewsDesks;
    public String mBeginDate;

    public enum NewsDesks {
        ARTS ("Arts"),
        FASHION_AND_STYLE ("Fashion & Style"),
        SPORTS ("Sports");

        private final String mName;

        NewsDesks(String name) {
            mName = name;
        }

        public String toString() {
            return this.mName;
        }
    }

    public NYTimesSearchFilter() {
        mSortOrder = NYTimesSortOrder.NEWEST;
        mNewsDesks = EnumSet.noneOf(NewsDesks.class);
        mBeginDate = "";
    }

    public void setSortOrder(NYTimesSortOrder sortOrder) {
        this.mSortOrder = sortOrder;
    }

    public NYTimesSortOrder getSortOrder() {
        return this.mSortOrder;
    }

    public void updateNewsDesk(NewsDesks newsDesk, boolean add) {
        if (add == true) {
            addNewsDesk(newsDesk);
        } else {
            removeNewsDesk(newsDesk);
        }
    }
    public void addNewsDesk(NewsDesks newsDesk) {
        this.mNewsDesks.add(newsDesk);
    }

    public void removeNewsDesk(NewsDesks newsDesk) {
        this.mNewsDesks.remove(newsDesk);
    }

    public boolean isNewsDeskEnabled(NewsDesks newsDesk) {
        return this.mNewsDesks.contains(newsDesk);
    }

    public String getNewsDesksAsQueryString() {
        if (mNewsDesks.size() == 0) {
            return "";
        }
        StringBuilder newsDeskQueryString = new StringBuilder("(");
        for (NewsDesks nd : mNewsDesks) {
            newsDeskQueryString.append("\"" + nd.toString() + "\" ");
        }
        newsDeskQueryString.append(")");
        String queryString = newsDeskQueryString.toString();
        return queryString;
    }

    public String getBeginDateAsString() {
        return this.mBeginDate;
    }

    public String getBeginDateAsQueryString() {
        String currentDate = this.mBeginDate;
        String day, month, year;
        String split[] = currentDate.split("/");
        if (split.length == 1) {
            return "";
        }
        month = split[0];
        if (month.length() == 1) {
            month = "0" + month;
        }
        day = split[1];
        if (day.length() == 1) {
            day = "0" + day;
        }
        year = split[2];
        return year + month + day;
    }

    public void setBeginDate(String beginDate) {
        this.mBeginDate = beginDate;
    }
}
