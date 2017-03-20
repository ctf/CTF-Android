package ca.mcgill.science.ctf.requests;

import com.google.gson.Gson;

import ca.mcgill.science.ctf.tepid.Session;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * request a Session from TEPID, the Session's token will be used for making
 * authenticated requests to TEPID
 */
public class ColorRequest extends BaseTepidRequest<Session> {

    private static boolean color;
    private static String token, username;
    private static final String url = baseUrl + "users/"+username+"/"+color;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * create a LoginRequest to execute
     * @param username the username used to authenticate with TEPID (mcgill email or short user)
     * @param color the boolean to toggle color
     * @param token the token
     */
    public ColorRequest(boolean color, String token, String username) {
        super(Session.class);
        this.username = username;
        this.color = color;
        this.token = token;
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {

        // create a POST request with our JSON serialized SessionRequest
        Request put = new Request.Builder()
                .url(url)
                .put(RequestBody.create(JSON, new Gson().toJson(color)))
                .build();

        // execute the request
        Response response = getOkHttpClient()
                .newCall(put)
                .execute();

        // check response status is between 200 & 300
        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        // deserialize and return TEPID's response
        return new Gson().fromJson(response.body().string(), Session.class);
    }
}
