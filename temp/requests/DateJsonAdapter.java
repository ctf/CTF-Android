package ca.mcgill.science.ctf.requests;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

//todo this should work even if the date string isn't a long, i.e., should extend DateTypeAdapter functionality, not replace it

/**
 * custom TypeAdapter to deal with serializing/deserializing variously formatted dates in TEPID responses
 */
public class DateJsonAdapter extends TypeAdapter<Date> {

    /**
     * serializes a date to json, will mainly be used for caching data from TEPID locally on the phone
     * @param out where we write our serialized json
     * @param value the value to serialize
     * @throws IOException
     */
    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (null == value) {
            out.nullValue(); // if the date is null we write out a null value
            return;
        }

        out.value(value.getTime()); // else we write a long value representing the date
    }

    /**
     * deserialize the json in "in" to a date object
     * @param in
     * @return
     * @throws IOException
     */
    @Override
    public Date read(JsonReader in) throws IOException {
        Date out = null;

        // check the next json element is not null
        if(in.peek() == JsonToken.NULL) {
             in.nextNull();
             return null;
        }

        // get the next json element
        String json = in.nextString();
        try {
            out = new Date(Long.parseLong(json)); // try to parse it as a long and make a date object
        } catch (Exception e) {
            try {
                // todo make sure these are the parameters we want for getDateTimeInstance to parse the datetime format tepid uses
                DateFormat.getDateTimeInstance(2, 2, Locale.CANADA).parse(json); // else maybe the date is formatted some other way
            } catch (Exception e1) {
                throw new JsonSyntaxException(json, e1);
            }
        }

        return out;
    }
}
