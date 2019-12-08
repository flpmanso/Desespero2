package com.example.desespero2;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface JsonPlaceHolderApi {
    @GET("/marcacao")
    Call<List<Post>> getPosts(
            @Query("userId") Integer[] userId,
            @Query("_sort") String sort,
            @Query("_order") String order
    );

    @GET("/marcacao")
    Call<List<Post>> getPosts();

    @GET("/marcacao/{id}")
    Call<Post> getComments(@Path("id") int postId);

    @GET
    Call<List<Comment>> getComments(@Url String url);

    @POST("/marcacao")
    Call<Post> createPost(@Body Post post);

    @POST("/marcacao")
    Call<Post> createPost(
            @Field("") int userId,
            @Field("title") String title,
            @Field("body") String text
    );

    @FormUrlEncoded
    @POST("/marcacao")
    Call<Post> createPost(@FieldMap Map<String, String> fields);

    @Headers({"Static-Header1: 123", "Static-Header2: 456"})
    @PUT("/marcacao/{id}")
    Call<Post> putPost(@HeaderMap Map<String, String> headers,
                       @Path("id") int id,
                       @Body Post post);

    @PATCH("/marcacao/{id}")
    Call<Post> patchPost(@HeaderMap Map<String, String> headers,
                         @Path("id") int id,
                         @Body Post post);

    @DELETE("/marcacao/{id}")
    Call<Void> deletePost(@Path("id") int id);
}
