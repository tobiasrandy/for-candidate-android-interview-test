package com.tokopedia.testproject.problems.news.view;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.underscore.U;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.tokopedia.testproject.R;
import com.tokopedia.testproject.problems.news.model.ArticleModel;
import com.tokopedia.testproject.problems.news.presenter.NewsPresenter;

import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;

public class NewsActivity extends AppCompatActivity implements com.tokopedia.testproject.problems.news.presenter.NewsPresenter.View {

    private NewsPresenter newsPresenter;
    private NewsAdapter newsAdapter;
    private RelativeLayout progressDialog;
    private LinearLayout retryBar;
    private LinearLayout emptyNews;
    private CarouselView carouselView;
    private Toolbar toolbar;
    private VerticalRecyclerView recyclerView;
    private MaterialSearchView searchView;
    private NestedScrollView nestedScrollView;
    private String keyword = "android";
    private ArrayList<String> urls = new ArrayList<>();
    private static final String DATE_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String SIMPLE_DATE_FORMAT = "d MMMM yyyy";
    private int currentPage = 1;
    private LinearLayoutManager linearLayoutManager;
    private String lastIndexDate = "";
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        toolbar = findViewById(R.id.toolbarNews);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();
        newsPresenter = new NewsPresenter(this);
        newsAdapter = new NewsAdapter(null);
        recyclerView = findViewById(R.id.recyclerView);
        linearLayoutManager = recyclerView.getLinearLayoutManager();
        progressDialog = findViewById(R.id.progressDialog);
        emptyNews = findViewById(R.id.emptyNews);
        carouselView = findViewById(R.id.caouselView);
        searchView = findViewById(R.id.searchView);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        retryBar = findViewById(R.id.retryBar);

        carouselView.setPageCount(urls.size());
        carouselView.setImageListener(new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                if(urls.size() > 0){
                    RequestOptions options = new RequestOptions();
                    options.fitCenter();
                    Glide.with(getApplicationContext()).load(urls.get(position)).apply(options).into(imageView);
                }
            }
        });


        TextView buttonRetry = findViewById(R.id.btnRetry);
        buttonRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retryBar.setVisibility(View.GONE);
                fetchData(1);
            }
        });

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(linearLayoutManager.getChildAt(linearLayoutManager.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {

                        int visibleItemCount = linearLayoutManager.getChildCount();
                        int totalItemCount = linearLayoutManager.getItemCount();
                        int pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount && isNetworkAvailable()) {
                            fetchData(currentPage+1);
                        }

                    }
                }
            }
        });

        recyclerView.setAdapter(newsAdapter);
        fetchData(1);
    }

    public void fetchData(int page){
        if(isNetworkAvailable() || realm.where(ArticleModel.class).findAll().isEmpty()){
            newsPresenter.getEverything(keyword, page);
        }
        else{
            emptyNews.setVisibility(View.GONE);
            carouselView.setVisibility(View.VISIBLE);

            List<ArticleModel> articlesList = realm.where(ArticleModel.class).equalTo("isHeadline", false).findAll();
            newsAdapter.addAllArticles(articlesList);
            newsAdapter.notifyDataSetChanged();
            keyword = articlesList.get(0).getKeyword();

            //to refresh list when internet is available and pulling data from lazy load
            currentPage = 0;

            urls.clear();
            List<ArticleModel> headlinesList = realm.where(ArticleModel.class).equalTo("isHeadline", true).findAll();
            int bannerSize = headlinesList.size() > 5 ? 5 : headlinesList.size();
            for (int i = 0; i < bannerSize; i++) {
                urls.add(headlinesList.get(i).getUrlToImage());
            }
            carouselView.setPageCount(urls.size());

            hideProgressDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        MenuItem searchMenu = menu.findItem(R.id.nav_search);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (isNetworkAvailable()) {
                    keyword = query;
                    fetchData(1);
                }
                else{
                    Toast.makeText(getApplicationContext(), "You are currently offline. Please go online to use this feature", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setMenuItem(searchMenu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSuccessGetNews(List<ArticleModel> articleList, int page) {
        if(page == 1) {
            currentPage = 1;
            nestedScrollView.smoothScrollTo(0, 0);
            if (articleList.size() > 0) {
                emptyNews.setVisibility(View.GONE);
                List<ArticleModel> articles = formatAndGroupTheArticles(articleList, page);
                newsAdapter.setArticleList(articles);
                newsAdapter.notifyDataSetChanged();

                realm.beginTransaction();
                realm.deleteAll();
                realm.insert(articles);
                realm.commitTransaction();

                newsPresenter.getTopHeadlines();
            } else {
                hideProgressDialog();
                newsAdapter.clearArticles();
                carouselView.setVisibility(View.GONE);
                emptyNews.setVisibility(View.VISIBLE);
            }
        }
        else {
            currentPage = page;
            if(articleList.size() > 0) {
                List<ArticleModel> articles = formatAndGroupTheArticles(articleList, page);
                newsAdapter.addAllArticles(articles);
                newsAdapter.notifyDataSetChanged();

                realm.beginTransaction();
                realm.insert(articles);
                realm.commitTransaction();
            }
            newsPresenter.getTopHeadlines();
        }
    }

    @Override
    public void onErrorGetNews(Throwable throwable) {
        hideProgressDialog();
        if(throwable instanceof UnknownHostException || throwable instanceof NetworkErrorException) {
            retryBar.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            retryBar.startAnimation(slideUp);
        }
        else {throwable.printStackTrace();
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccessGetHeadlines(List<ArticleModel> headlinesList) {
        hideProgressDialog();
        if(headlinesList.size() > 0){
            carouselView.setVisibility(View.VISIBLE);
            urls.clear();
            int bannerSize = headlinesList.size() > 5 ? 5 : headlinesList.size();
            List<ArticleModel> realmHeadlinesList = new ArrayList<>();
            for (int i = 0; i < bannerSize; i++) {
                headlinesList.get(i).setHeadline(true);
                urls.add(headlinesList.get(i).getUrlToImage());
                realmHeadlinesList.add(headlinesList.get(i));
            }

            realm.beginTransaction();
            if(realm.where(ArticleModel.class).equalTo("isHeadline", true).findAll().size() > 0){
                realm.where(ArticleModel.class).equalTo("isHeadline", true).findAll().deleteAllFromRealm();
            }
            realm.insert(realmHeadlinesList);
            realm.commitTransaction();

            carouselView.setPageCount(urls.size());
        }
    }

    @Override
    public void onErrorGetHeadlines(Throwable throwable) {
        hideProgressDialog();
        if(throwable instanceof UnknownHostException || throwable instanceof NetworkErrorException) {
            retryBar.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);
            retryBar.startAnimation(slideUp);
        }
        else {throwable.printStackTrace();
            Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onApiInitialize() {
        showProgressDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        newsPresenter.unsubscribe();
        realm.close();
    }

    public void showProgressDialog(){
        progressDialog.setVisibility(View.VISIBLE);
    }

    public void hideProgressDialog(){
        progressDialog.setVisibility(View.GONE);
    }

    public List<ArticleModel> formatAndGroupTheArticles(List<ArticleModel> articles, int page){
        Collections.sort(articles, new Comparator<ArticleModel>() {
            @Override
            public int compare(ArticleModel o1, ArticleModel o2) {
                return o2.getPublishedAt().compareTo(o1.getPublishedAt());
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_WITH_TIMEZONE, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        for (int i = 0; i < articles.size(); i++) {
            try{
                Date date = new SimpleDateFormat(DATE_FORMAT_WITH_TIMEZONE).parse(articles.get(i).getPublishedAt());
                articles.get(i).setPublishedAt(new SimpleDateFormat(SIMPLE_DATE_FORMAT).format(date));
            } catch (ParseException e){
                e.printStackTrace();
            }
        }

        List<String> dateValues = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            dateValues.add(articles.get(i).getPublishedAt());
        }
        dateValues = U.uniq(dateValues);
        if(dateValues.get(0).equals(lastIndexDate) && page != 1){
            dateValues.remove(0);
        }

        if(dateValues.size() > 0) {
            int counter = 0;
            for (int i = 0; i < articles.size(); i++) {
                if (articles.get(i).getPublishedAt().equals(dateValues.get(counter))) {
                    ArticleModel article = new ArticleModel();
                    article.setPublishedAt(dateValues.get(counter));
                    article.setItemType("date");
                    articles.add(i, article);
                    counter++;
                    if (counter == dateValues.size()) {
                        break;
                    }
                }
            }
            lastIndexDate = dateValues.get(dateValues.size()-1);
        }

        return setArticlesKeywordAndPage(articles);
    }

    public List<ArticleModel> setArticlesKeywordAndPage(List<ArticleModel> articles){
        for (int i = 0; i < articles.size(); i++) {
            articles.get(i).setKeyword(keyword);
        }
        return articles;
    }

    @Override
    public void onBackPressed() {
        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else{
            super.onBackPressed();
        }
    }

    public boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
