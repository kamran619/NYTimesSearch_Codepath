package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

/**
 * Created by kpirwani on 2/12/16.
 */
public class NYTimesSearchModel {

    Response response;

    public class Docs {
        String web_url;
        String snippet;
        String lead_paragraph;
        String source;
        Multimedia[] multimedia;
        Headline headline;
        Keyword[] keywords;
        String pub_date;
        String document_type;
        String news_desk;
        String subsection_name;
        String type_of_material;
        String _id;
        String word_count;
        String slideshow_credits;

        public Multimedia getThumbnailMultimediaOrBest() {
            String subtype = "wide";
            for (int i = 0; i < multimedia.length; i++) {
                Multimedia mm = multimedia[i];
                if (mm.subtype.equalsIgnoreCase(subtype)) {
                    return mm;
                }
            }
            subtype = "thumbnail";
            for (int i = 0; i < multimedia.length; i++) {
                Multimedia mm = multimedia[i];
                if (mm.subtype.equalsIgnoreCase(subtype)) {
                    return mm;
                }
            }
            if (multimedia.length > 0) {
                return multimedia[0];
            }
            return null;
        }
    }

    public class Meta {
        long hits;
        long time;
        long offset;
    }

    public class Response {
        public Meta meta;
        public Docs[] docs;
        String status;
        String copyright;
    }

    public class Multimedia {
        int width;
        int height;
        String url;
        String subtype;
        String type;
    }

    public class Headline {
        String main;
    }

    public class Keyword {
        String rank;
        boolean is_major;
        String name;
        String value;
    }
}
