package ca.mcgill.science.ctf.requests;

import com.google.gson.Gson;

import ca.mcgill.science.ctf.tepid.Session;
import ca.mcgill.science.ctf.tepid.SessionRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * request a Session from TEPID, the Session's token will be used for making
 * authenticated requests to TEPID
 */
public class LoginRequest extends BaseTepidRequest<Session> {

    private static final String url = baseUrl + "sessions/";
    private String username, password;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * create a LoginRequest to execute
     * @param username the username used to authenticate with TEPID (mcgill email or short user)
     * @param password the matching password
     */
    public LoginRequest(String username, String password) {
        super(Session.class);
        this.username = username;
        this.password = password;
    }

    @Override
    public Session loadDataFromNetwork() throws Exception {

        // create the session request object expected by TEPID, fill with our data
        SessionRequest sr = new SessionRequest()
                .withUsername(username)
                .withPassword(password)
                .withPermanent(true)
                .withPersistent(true);

        // create a POST request with our JSON serialized SessionRequest
        Request request= new Request.Builder()
                .url(url)
                .post(RequestBody.create(JSON, new Gson().toJson(sr)))
                .build();

        // execute the request
        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        // check response status is between 200 & 300
        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        // deserialize and return TEPID's response
        return new Gson().fromJson(response.body().string(), Session.class);
    }
}
