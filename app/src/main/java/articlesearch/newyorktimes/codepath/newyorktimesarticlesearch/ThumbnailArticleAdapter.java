package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by kpirwani on 2/12/16.
 */
public class ThumbnailArticleAdapter extends RecyclerView.Adapter<ThumbnailArticleAdapter.ViewHolder> {

    final public ArrayList<NYTimesSearchModel> mArticles;
    private Context mApplicationContext;
    private IRecyclerViewOnItemClicked mOnItemClickedCallback;

    public interface IRecyclerViewOnItemClicked {
        public void onItemClicked(int position);
    }
    public ThumbnailArticleAdapter(Context applicationContext, ArrayList<NYTimesSearchModel> list, IRecyclerViewOnItemClicked callback) {
        mApplicationContext = applicationContext;
        mArticles = list;
        mOnItemClickedCallback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView ivThumbnail;
        public TextView tvThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvThumbnail = (TextView) itemView.findViewById(R.id.tvThumbnail);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            mOnItemClickedCallback.onItemClicked(position);
        }


    }

    @Override
    public ThumbnailArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View thumbnailView = inflater.inflate(R.layout.item_thumbnail_article, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(thumbnailView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ThumbnailArticleAdapter.ViewHolder viewHolder, int position) {
        NYTimesSearchModel model = mArticles.get(0);
        NYTimesSearchModel.Docs article = model.response.docs[position];

        TextView textView = viewHolder.tvThumbnail;
        textView.setText(article.headline.main.toString());

        ImageView imageView = viewHolder.ivThumbnail;
        NYTimesSearchModel.Multimedia mm = article.getThumbnailMultimediaOrBest();
        if (mm !=null && mm.url != null) {
            String url = "http://www.nytimes.com/" + mm.url;
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            params.width = mm.width;
            params.height = mm.height;
            imageView.setLayoutParams(params);
            Glide.with(mApplicationContext).load(url).into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        int length = 0;
        if (mArticles.size() > 0) {
            NYTimesSearchModel model = mArticles.get(0);
            if (model != null) {
                if (model.response != null) {
                    length = model.response.docs.length;
                }
            }
        }
        return length;
    }
}
