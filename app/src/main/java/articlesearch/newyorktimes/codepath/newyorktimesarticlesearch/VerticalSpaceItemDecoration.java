package articlesearch.newyorktimes.codepath.newyorktimesarticlesearch;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kpirwani on 2/14/16.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mOffset;

    public VerticalSpaceItemDecoration(int offset) {
        this.mOffset = offset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mOffset;
        outRect.left = mOffset;
        outRect.right = mOffset;
        outRect.top = mOffset;
    }
}