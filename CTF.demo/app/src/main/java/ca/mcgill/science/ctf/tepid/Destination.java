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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.gson.annotations.JsonAdapter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import ca.mcgill.science.ctf.requests.DateJsonAdapter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "_id", "_rev", "type", "name", "protocol", "username", "password", "path", "domain" })
public class Destination implements Parcelable {

	@JsonProperty("_id")
	private String _id;
	@JsonProperty("_rev")
	private String _rev;
	@JsonProperty("type")
	private String type;
	@JsonProperty("name")
	private String name;
	@JsonProperty("protocol")
	private String protocol;
	@JsonProperty("username")
	private String username;
	@JsonProperty("password")
	private String password;
	@JsonProperty("path")
	private String path;
	@JsonProperty("domain")
	private String domain;
	@JsonProperty("ticket")
	private DestinationTicket ticket;
	@JsonProperty("up")
	private boolean up;
	@JsonProperty("ppm")
	private int ppm;
	@JsonIgnore
	private transient Map<String, Object> additionalProperties = new HashMap<String, Object>();

	/**
	 * 
	 * @return The Id
	 */
	@JsonProperty("_id")
	public String getId() {
		return _id;
	}

	/**
	 * 
	 * @param _id
	 *            The _id
	 */
	@JsonProperty("_id")
	public void setId(String _id) {
		this._id = _id;
	}

	/**
	 * 
	 * @return The Rev
	 */
	@JsonProperty("_rev")
	public String getRev() {
		return _rev;
	}

	/**
	 * 
	 * @param _rev
	 *            The _rev
	 */
	@JsonProperty("_rev")
	public void setRev(String _rev) {
		this._rev = _rev;
	}

	/**
	 * 
	 * @return The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 *            The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return The protocol
	 */
	@JsonProperty("protocol")
	public String getProtocol() {
		return protocol;
	}

	/**
	 * 
	 * @param protocol
	 *            The protocol
	 */
	@JsonProperty("protocol")
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * 
	 * @return The username
	 */
	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @param username
	 *            The username
	 */
	@JsonProperty("username")
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 
	 * @return The password
	 */
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @param password
	 *            The password
	 */
	@JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 
	 * @return The path
	 */
	@JsonProperty("path")
	public String getPath() {
		return path;
	}

	/**
	 * 
	 * @param path
	 *            The path
	 */
	@JsonProperty("path")
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 
	 * @return The domain
	 */
	@JsonProperty("domain")
	public String getDomain() {
		return domain;
	}

	/**
	 * 
	 * @param domain
	 *            The domain
	 */
	@JsonProperty("domain")
	public void setDomain(String domain) {
		this.domain = domain;
	}

	@JsonProperty("ticket")
	public DestinationTicket getTicket() {
		return ticket;
	}

	@JsonProperty("ticket")
	public void setTicket(DestinationTicket ticket) {
		this.ticket = ticket;
	}

	@JsonProperty("up")
	public boolean isUp() {
		return up;
	}

	@JsonProperty("up")
	public void setUp(boolean up) {
		this.up = up;
	}

	@JsonProperty("ppm")
	public int getPpm() {
		return ppm;
	}
	
	@JsonProperty("ppm")
	public void setPpm(int ppm) {
		this.ppm = ppm;
	}

	@Override
	public String toString() {
		return "Destination [_id=" + _id + ", type=" + type + ", name=" + name + ", protocol=" + protocol
				+ ", username=" + username + ", password=" + password + ", path=" + path + ", domain=" + domain
				+ ", ticket=" + ticket + ", up=" + up + ", additionalProperties=" + additionalProperties + "]";
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}
	
	
	@JsonInclude(Include.NON_NULL)
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class DestinationTicket implements Parcelable {
		public boolean up;
		public String reason;
		public LdapUser user;
		@JsonAdapter(DateJsonAdapter.class)
		public Date reported = new Date();

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeByte(this.up ? (byte) 1 : (byte) 0);
			dest.writeString(this.reason);
			dest.writeParcelable(this.user, flags);
			dest.writeLong(this.reported != null ? this.reported.getTime() : -1);
		}

		public DestinationTicket() {
		}

		protected DestinationTicket(Parcel in) {
			this.up = in.readByte() != 0;
			this.reason = in.readString();
			this.user = in.readParcelable(LdapUser.class.getClassLoader());
			long tmpReported = in.readLong();
			this.reported = tmpReported == -1 ? new Date() : new Date(tmpReported);
		}

		public static final Creator<DestinationTicket> CREATOR = new Creator<DestinationTicket>() {
			@Override
			public DestinationTicket createFromParcel(Parcel source) {
				return new DestinationTicket(source);
			}

			@Override
			public DestinationTicket[] newArray(int size) {
				return new DestinationTicket[size];
			}
		};
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this._id);
		dest.writeString(this._rev);
		dest.writeString(this.type);
		dest.writeString(this.name);
		dest.writeString(this.protocol);
		dest.writeString(this.username);
		dest.writeString(this.password);
		dest.writeString(this.path);
		dest.writeString(this.domain);
		dest.writeParcelable(this.ticket, flags);
		dest.writeByte(this.up ? (byte) 1 : (byte) 0);
		dest.writeInt(this.ppm);
	}

	public Destination() {
	}

	protected Destination(Parcel in) {
		this._id = in.readString();
		this._rev = in.readString();
		this.type = in.readString();
		this.name = in.readString();
		this.protocol = in.readString();
		this.username = in.readString();
		this.password = in.readString();
		this.path = in.readString();
		this.domain = in.readString();
		this.ticket = in.readParcelable(DestinationTicket.class.getClassLoader());
		this.up = in.readByte() != 0;
		this.ppm = in.readInt();
	}

	public static final Parcelable.Creator<Destination> CREATOR = new Parcelable.Creator<Destination>() {
		@Override
		public Destination createFromParcel(Parcel source) {
			return new Destination(source);
		}

		@Override
		public Destination[] newArray(int size) {
			return new Destination[size];
		}
	};
}