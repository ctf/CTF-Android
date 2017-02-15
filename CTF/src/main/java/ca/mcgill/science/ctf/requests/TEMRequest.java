package ca.mcgill.science.ctf.requests;

import org.json.JSONArray;

import okhttp3.Request;
import okhttp3.Response;

public class TEMRequest extends BaseTepidRequest<JSONArray> {

    private static String url;
    private String token;

    public TEMRequest(String token) {
        super(JSONArray.class);
        this.token = token;
        url = baseUrl + "endpoints/";
    }

    @Override
    public JSONArray loadDataFromNetwork() throws Exception {
        Request request = new Request.Builder()
                .header("Authorization", "Token " + token)
                .url(url)
                .build();

        Response response = getOkHttpClient()
                .newCall(request)
                .execute();

        if (!response.isSuccessful()) {
            throw new Exception("UH OH AN ERROR OCCURRED!!!!!!!!!");
        }

        return new JSONArray(response.body().string());
    }

}
