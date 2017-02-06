/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import com.github.simonpercic.oklog3.OkLogInterceptor;

import javax.inject.Inject;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import timber.log.Timber;

/**
 * TODO: Generic description. Replace with real one.
 */
class FeedbackRestClient {
    private static final String BASE_URL = "http://www.android-feedback.com";

    private FeedbackRestService restService;
    private boolean isConfigured;

    FeedbackRestClient() {}

    private void setupConnection() {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
        setupInterceptors(httpBuilder);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(new ToStringConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(httpBuilder.build())
                .build();

        restService = retrofit.create(FeedbackRestService.class);
        isConfigured = true;
    }

    /**
     * Setups interceptors for retrofit client.
     *
     * @param httpBuilder OkHttpClient.Builder that will be used in Retrofit.Builder
     */
    private void setupInterceptors(OkHttpClient.Builder httpBuilder) {
        httpBuilder.addInterceptor((chain -> {
            Request original = chain.request();

            Request.Builder requestBuilder = original.newBuilder()
                                                     .header("Accept", "*/*")
                                                     .method(original.method(), original.body());

            Request request = requestBuilder.build();
            return chain.proceed(request);
        }));

        httpBuilder.addInterceptor((chain -> {
            Request request = chain.request();
            HttpUrl url = request.url().newBuilder()
                                 .build();
            request = request.newBuilder().url(url).build();
            return chain.proceed(request);
        }));
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        httpBuilder.addInterceptor(loggingInterceptor);
        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        httpBuilder.addInterceptor(okLogInterceptor);
    }

    /**
     * Checks if retrofit client is set. If not setup is done automatically.
     */
    private void checkConfigurationValid() {
        if (!isConfigured) {
            Timber.d("Server connection not established. Setup connection");
            setupConnection();
        }
    }

    Observable<String> getPendingReplies(String uuid) {
        try {
            checkConfigurationValid();
            return restService.getPendingReplies(uuid);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    Observable<String> postFeedback(String json) {
        try {
            checkConfigurationValid();
            return restService.postFeedback(json);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }
}
