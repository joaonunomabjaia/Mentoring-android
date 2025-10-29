package mz.org.csaude.mentoring.adapter.recyclerview.generic;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.base.model.BaseModel;
import mz.org.csaude.mentoring.listner.recyclerView.IOnLoadMoreListener;


public abstract class AbstractRecycleViewAdapter<T extends BaseModel> extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    protected List<T> records = new ArrayList<>();

    protected final int VIEW_TYPE_ITEM = 0;
    protected final int VIEW_TYPE_LOADING = 1;
    protected BaseActivity activity;
    protected IOnLoadMoreListener onLoadMoreListener;
    protected boolean isLoading;
    protected int visibleThreshold = 5;
    protected int lastVisibleItem, totalItemCount;

    protected int selectedPosition = RecyclerView.NO_POSITION;

    public AbstractRecycleViewAdapter(RecyclerView recyclerView, List<T> records, BaseActivity activity) {
        this.records = records;
        this.activity = activity;

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager && records != null && !records.isEmpty()) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && linearLayoutManager.findLastCompletelyVisibleItemPosition() == records.size() - 1) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            }
        });
    }

    public void updateData(List<T> newRecords) {
        this.records.clear();   // Clear the old data
        this.records.addAll(newRecords);   // Add the new data
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    public void setOnLoadMoreListener(IOnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public BaseActivity getActivity() {
        return this.activity;
    }

    public IOnLoadMoreListener getOnLoadMoreListener() {
        return onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return records.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return (records != null) ? records.size() : 0;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public boolean isLoading() {
        return isLoading;
    }

    /** Troca a lista exibida sem recriar o adapter */
    public void submitList(@NonNull List<T> items) {
        this.records.clear();
        this.records.addAll(items);
        notifyDataSetChanged();
    }
}
