/*
* author: Bartlomiej Kierys
* date: 2017-01-31
* email: bskierys@gmail.com
*/
package pl.ipebk.tabi.feedback;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * TODO: Generic description. Replace with real one.
 */
public interface FeedbackRestService {
    @FormUrlEncoded
    @POST("/service/2") Observable<String> postFeedback(@Field("json") String feedbackJson);

    @GET("/service/2/{uuid}") Observable<String> getPendingReplies(@Path("uuid") String uuid);
}
