package com.ctf.mcgill.tepid;

import com.ctf.mcgill.requests.DateJsonAdapter;
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

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "_id", "_rev", "type", "name", "protocol", "username", "password", "path", "domain" })
public class Destination {

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
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
	public static class DestinationTicket {
		public boolean up;
		public String reason;
		public LdapUser user;
		@JsonAdapter(DateJsonAdapter.class)
		public Date reported = new Date();
	}

}