package ca.mcgill.science.ctf.tepid;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.JsonAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.science.ctf.requests.DateJsonAdapter;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintJob implements Parcelable {
    public transient final String type = "job";
    private String name, queueName, originalHost, userIdentification, destination;
    private int colorPages, pages;
    @JsonAdapter(DateJsonAdapter.class)
    public Date started = new Date();
    @JsonAdapter(DateJsonAdapter.class)
    private Date processed;
    @JsonAdapter(DateJsonAdapter.class)
    private Date printed;
    private long eta;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getOriginalHost() {
        return originalHost;
    }

    public void setOriginalHost(String originalHost) {
        this.originalHost = originalHost;
    }

    public String getUserIdentification() {
        return userIdentification;
    }

    public void setUserIdentification(String userIdentification) {
        this.userIdentification = userIdentification;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getColorPages() {
        return colorPages;
    }

    public void setColorPages(int colorPages) {
        this.colorPages = colorPages;
    }

    public Date getProcessed() {
        return processed;
    }

    public void setProcessed(Date processed) {
        this.processed = processed;
    }

    public Date getPrinted() {
        return printed;
    }

    public void setPrinted(Date printed) {
        this.printed = printed;
    }

    public long getEta() {
        return eta;
    }

    public void setEta(long eta) {
        this.eta = eta;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    private String _id, _rev;
    @JsonIgnore
    private transient Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_id")
    public String getId() {
        return _id;
    }

    @JsonProperty("_id")
    public void setId(String _id) {
        this._id = _id;
    }

    @JsonProperty("_rev")
    public String getRev() {
        return _rev;
    }

    @JsonProperty("_rev")
    public void setRev(String _rev) {
        this._rev = _rev;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintJob [name=").append(name).append(", queueName=")
                .append(queueName).append(", originalHost=")
                .append(originalHost).append(", userIdentification=")
                .append(userIdentification).append("]");
        return builder.toString();
    }

    public PrintJob() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.queueName);
        dest.writeString(this.originalHost);
        dest.writeString(this.userIdentification);
        dest.writeString(this.destination);
        dest.writeInt(this.colorPages);
        dest.writeInt(this.pages);
        dest.writeLong(this.started != null ? this.started.getTime() : -1);
        dest.writeLong(this.processed != null ? this.processed.getTime() : -1);
        dest.writeLong(this.printed != null ? this.printed.getTime() : -1);
        dest.writeLong(this.eta);
        dest.writeString(this._id);
        dest.writeString(this._rev);
    }

    protected PrintJob(Parcel in) {
        this.name = in.readString();
        this.queueName = in.readString();
        this.originalHost = in.readString();
        this.userIdentification = in.readString();
        this.destination = in.readString();
        this.colorPages = in.readInt();
        this.pages = in.readInt();
        long tmpStarted = in.readLong();
        this.started = tmpStarted == -1 ? new Date() : new Date(tmpStarted);
        long tmpProcessed = in.readLong();
        this.processed = tmpProcessed == -1 ? null : new Date(tmpProcessed);
        long tmpPrinted = in.readLong();
        this.printed = tmpPrinted == -1 ? null : new Date(tmpPrinted);
        this.eta = in.readLong();
        this._id = in.readString();
        this._rev = in.readString();
    }

    public static final Creator<PrintJob> CREATOR = new Creator<PrintJob>() {
        @Override
        public PrintJob createFromParcel(Parcel source) {
            return new PrintJob(source);
        }

        @Override
        public PrintJob[] newArray(int size) {
            return new PrintJob[size];
        }
    };
}
