package ca.mcgill.science.ctf.services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Allan Wang on 18/03/2017.
 * <p>
 * Refs
 * https://github.com/ruler88/GithubDemo
 * https://zeroturnaround.com/rebellabs/getting-started-with-retrofit-2/
 */

public interface TEPIDService {
    String SERVICE_ENDPOINT = "https://tepid.sus.mcgill.ca:8443/tepid/";

    @GET("/users/{login}")
    Observable<Github> getUser(@Path("login") String login);

    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
