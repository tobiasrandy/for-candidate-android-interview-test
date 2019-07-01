package com.tokopedia.testproject.problems.news.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.ArticleModel;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private List<ArticleModel> articleList;

    NewsAdapter(List<ArticleModel> articleList) {
        setArticleList(articleList);
    }

    void setArticleList(List<ArticleModel> articleList) {
        if (articleList == null) {
            this.articleList = new ArrayList<>();
        } else {
            this.articleList.clear();
            this.articleList.addAll(articleList);
        }
    }

    void addAllArticles(List<ArticleModel> articleList) {
        this.articleList.addAll(articleList);
    }

    void clearArticles(){
        this.articleList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NewsViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_news, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, int i) {
        newsViewHolder.bind(articleList.get(i));
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        //news view
        CardView cardView;
        LinearLayout newsContainer;
        ImageView imageView;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvDate;

        //date view
        RelativeLayout dateContainer;
        TextView tvDateGroup;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.newsCardView);
            newsContainer = itemView.findViewById(R.id.newsContainer);
            imageView = itemView.findViewById(R.id.iv);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            dateContainer = itemView.findViewById(R.id.dateContainer);
            tvDateGroup = itemView.findViewById(R.id.tvDateGroup);
        }

        void bind(ArticleModel article) {
            if(article.getItemType().equals("news")) {
                ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                cardViewMarginParams.setMargins(16, 0, 16, 0);
                cardView.requestLayout();

                dateContainer.setVisibility(View.GONE);
                newsContainer.setVisibility(View.VISIBLE);
                Glide.with(itemView).load(article.getUrlToImage()).into(imageView);
                tvTitle.setText(article.getTitle());
                tvDescription.setText(article.getDescription());
//                tvDate.setText(article.getPublishedAt());
            }
            else {
                ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                cardViewMarginParams.setMargins(0, 0, 0, 0);
                cardView.requestLayout();

                newsContainer.setVisibility(View.GONE);
                dateContainer.setVisibility(View.VISIBLE);
                tvDateGroup.setText(article.getPublishedAt());
            }
        }
    }
}
