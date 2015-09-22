package com.jiechic.library.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.jiechic.library.android.extendedrecyclerview.R;


public class ExtendedRecyclerView extends FrameLayout {

    protected int ITEM_LEFT_TO_LOAD_MORE = 10;

    protected SwipeRefreshLayout mPtrLayout;
    protected RecyclerView mRecycler;
    protected ViewStub mEmpty;
    protected View mEmptyView;

    protected boolean mClipToPadding;
    protected int mPadding;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mScrollbarStyle;
    protected int mEmptyId;

    protected RecyclerView.OnScrollListener mInternalOnScrollListener;
    protected RecyclerView.OnScrollListener mExternalOnScrollListener;

    final private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };


    public SwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }

    public RecyclerView getRecyclerView() {
        return mRecycler;
    }

    public ExtendedRecyclerView(Context context) {
        super(context);
        initView();
    }

    public ExtendedRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public ExtendedRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.extendedrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.extendedrecyclerview_recyclerClipToPadding, false);
            mPadding = (int) a.getDimension(R.styleable.extendedrecyclerview_recyclerPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.extendedrecyclerview_recyclerPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.extendedrecyclerview_recyclerPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.extendedrecyclerview_recyclerPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.extendedrecyclerview_recyclerPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.extendedrecyclerview_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.extendedrecyclerview_layout_empty, 0);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        LayoutInflater.from(getContext()).inflate(R.layout.layout_progress_recyclerview, this);
        mPtrLayout = (SwipeRefreshLayout) findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mEmpty = (ViewStub) findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);
        if (mEmptyId != 0)
            mEmptyView = mEmpty.inflate();
        mEmpty.setVisibility(View.GONE);

        initRecyclerView();
    }

    /**
     * Implement this method to customize the AbsListView
     */
    protected void initRecyclerView() {
        View recyclerView = findViewById(android.R.id.list);

        if (recyclerView instanceof RecyclerView)
            mRecycler = (RecyclerView) recyclerView;
        else
            throw new IllegalArgumentException("SuperRecyclerView works with a RecyclerView!");

        mRecycler.setHasFixedSize(true);
        mRecycler.setClipToPadding(mClipToPadding);
        mInternalOnScrollListener = new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mExternalOnScrollListener != null)
                    mExternalOnScrollListener.onScrollStateChanged(recyclerView, newState);

            }
        };
        mRecycler.addOnScrollListener(mInternalOnScrollListener);

        if (mPadding != -1.0f) {
            mRecycler.setPadding(mPadding, mPadding, mPadding, mPadding);
        } else {
            mRecycler.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
        }

        if (mScrollbarStyle != -1) {
            mRecycler.setScrollBarStyle(mScrollbarStyle);
        }

    }


    /**
     * Set the layout manager to the recycler
     *
     * @param manager
     */
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mRecycler.setLayoutManager(manager);
    }

    void checkIfEmpty() {
        if (mEmpty != null && getAdapter() != null && mEmpty.getLayoutResource() != 0) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmpty.setVisibility(emptyViewVisible ? VISIBLE : GONE);
        }
        mPtrLayout.setRefreshing(false);
    }

    public void setEmptyView(@LayoutRes int emptyViewId) {
        mEmpty.setLayoutResource(emptyViewId);
        mEmptyView = mEmpty.inflate();
        checkIfEmpty();
    }

    /**
     * Set the adapter to the recycler
     * Automatically hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyview is shown
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        final android.support.v7.widget.RecyclerView.Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        mRecycler.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }

        checkIfEmpty();
    }


    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     *
     * @param listener
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param colRes1
     * @param colRes2
     * @param colRes3
     * @param colRes4
     */
    public void setRefreshingColorResources(int colRes1, int colRes2, int colRes3, int colRes4) {
        mPtrLayout.setColorSchemeResources(colRes1, colRes2, colRes3, colRes4);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     *
     * @param col1
     * @param col2
     * @param col3
     * @param col4
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Set the scroll listener for the recycler
     *
     * @param listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mExternalOnScrollListener = listener;
    }

    /**
     * Add the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.addOnItemTouchListener(listener);
    }

    /**
     * Remove the onItemTouchListener for the recycler
     *
     * @param listener
     */
    public void removeOnItemTouchListener(RecyclerView.OnItemTouchListener listener) {
        mRecycler.removeOnItemTouchListener(listener);
    }

    /**
     * @return the recycler adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecycler.getAdapter();
    }


    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecycler.setOnTouchListener(listener);
    }

    public void setItemAnimator(RecyclerView.ItemAnimator animator) {
        mRecycler.setItemAnimator(animator);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.addItemDecoration(itemDecoration);
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration, int index) {
        mRecycler.addItemDecoration(itemDecoration, index);
    }

    public void removeItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecycler.removeItemDecoration(itemDecoration);
    }

    /**
     * @return inflated empty view or null
     */
    public View getEmptyView() {
        return mEmptyView;
    }

    public static enum LAYOUT_MANAGER_TYPE {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public abstract static class Adapter<VH extends android.support.v7.widget.RecyclerView.ViewHolder> extends android.support.v7.widget.RecyclerView.Adapter<VH> {

        private static final int TYPE_LOADMORE = Integer.MIN_VALUE;
        private static final int TYPE_ADAPTEE_OFFSET = 2;
        private boolean canLoadMore = false;
        private MoreViewHolder loadMoreHolder;
        private OnLoadListener listener;
        protected MoreViewHolder.OnLoadListener viewHolderListener = new MoreViewHolder.OnLoadListener() {
            @Override
            public void onLoad() {
                if (listener != null) {
                    listener.onLoad();
                }
            }
        };


        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_LOADMORE) {
                return (VH) onCreateLoadMoreItemViewHolder(parent, viewType);
            }
            return onCreateContentItemViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            if (position == getContentItemCount() && holder.getItemViewType() == TYPE_LOADMORE) {
                onBindLoadMoreItemView((MoreViewHolder) holder, position);
                loadMoreHolder = (MoreViewHolder) holder;
                loadMoreHolder.setLoadListener(viewHolderListener);
            } else {
                onBindContentItemView((VH) holder, position);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getContentItemCount() && canLoadMore) {
                return TYPE_LOADMORE;
            }
            return getContentItemType(position) + TYPE_ADAPTEE_OFFSET;
        }

        @Override
        public int getItemCount() {
            int itemCount = getContentItemCount();
            if (canLoadMore && itemCount != 0) {
                itemCount += 1;
            }
            return itemCount;
        }

        public void setCanLoadMore(boolean canLoadMore) {
            this.canLoadMore = canLoadMore;
            notifyDataSetChanged();
        }

        public abstract VH onCreateContentItemViewHolder(ViewGroup parent, int viewType);//创建你要的普通item

        public abstract void onBindContentItemView(VH holder, int position);//绑定数据

        public abstract int getContentItemCount();

        public abstract int getContentItemType(int position);//没用到，返回0即可，为了扩展用的

        public MoreViewHolder onCreateLoadMoreItemViewHolder(ViewGroup parent, int viewType)//创建你要的普通item
        {
            MoreViewHolder holder = new LoadViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_loadmore_item, parent, false));
            holder.setLoadListener(viewHolderListener);
            return holder;
        }

        public void onBindLoadMoreItemView(MoreViewHolder holder, int position)//绑定数据
        {

        }

        public void loadComplete() {
            if (loadMoreHolder != null) {
                loadMoreHolder.loadComplete();
            }
        }

        public interface OnLoadListener {
            void onLoad();
        }

        public void setOnLoadListener(OnLoadListener listener) {
            this.listener = listener;
        }


        public static class LoadViewHolder extends MoreViewHolder {

            Button button;
            ProgressBar progressbar;

            public LoadViewHolder(View itemView) {
                super(itemView);
                button = (Button) itemView.findViewById(R.id.button);
                progressbar = (ProgressBar) itemView.findViewById(R.id.progressbar);
                button.setOnClickListener((View v) -> {
                    loadMore();
                    button.setVisibility(View.GONE);
                    progressbar.setVisibility(View.VISIBLE);
                });
                button.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            protected void onLoadComplete() {
                button.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
            }
        }

    }

    public abstract static class MoreViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        private OnLoadListener loadListener;
        private boolean isLoad = false;

        public MoreViewHolder(View itemView) {
            super(itemView);
        }

        protected interface OnLoadListener {
            void onLoad();
        }

        protected void loadMore() {
            if (!isLoad && loadListener != null) {
                loadListener.onLoad();
                isLoad = true;
            }

        }

        public void loadComplete() {
            isLoad = false;
            onLoadComplete();
        }

        protected abstract void onLoadComplete();

        public void setLoadListener(OnLoadListener loadListener) {
            this.loadListener = loadListener;
        }

    }
}
