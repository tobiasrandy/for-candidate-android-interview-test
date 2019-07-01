package com.tokopedia.testproject.problems.news.presenter;

import android.accounts.NetworkErrorException;
import android.os.Build;

import com.tokopedia.testproject.BuildConfig;
import com.tokopedia.testproject.problems.news.model.ArticleModel;
import com.tokopedia.testproject.problems.news.model.NewsResultModel;
import com.tokopedia.testproject.problems.news.network.NewsDataSource;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hendry on 27/01/19.
 */
public class NewsPresenter {

    private CompositeDisposable composite = new CompositeDisposable();

    private View view;

    public interface View {
        void onSuccessGetNews(List<ArticleModel> articleList, int page);

        void onErrorGetNews(Throwable throwable);

        void onSuccessGetHeadlines(List<ArticleModel> headlinesList);

        void onErrorGetHeadlines(Throwable throwable);

        void onApiInitialize();
    }

    public NewsPresenter(NewsPresenter.View view) {
        this.view = view;
    }

    public void getEverything(String keyword, final int page) {
        view.onApiInitialize();
        NewsDataSource.getService().getEverything(keyword, BuildConfig.NEWS_API_KEY, "publishedAt", page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResultModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResultModel newsResultModel) {
                        view.onSuccessGetNews(newsResultModel.getArticles(), page);
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetNews(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getTopHeadlines(){
        NewsDataSource.getService().getTopHeadlines("us", "business", BuildConfig.NEWS_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<NewsResultModel>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        composite.add(d);
                    }

                    @Override
                    public void onNext(NewsResultModel newsResultModel) {
                        view.onSuccessGetHeadlines(newsResultModel.getArticles());
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onErrorGetHeadlines(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void unsubscribe() {
        composite.dispose();
    }
}
