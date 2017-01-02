package ca.mcgill.science.ctf.tepid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintQueue {
	@JsonProperty("type")
	public final String type = "queue";
	@JsonProperty("_id")
	public String _id;
	@JsonProperty("_rev")
	public String _rev;
	public String loadBalancer, defaultOn;
	public String name;
	public List<String> destinations;
	@Override
	public String toString() {
		return "PrintQueue [name=" + name + ", destinations=" + destinations + "]";
	}
	
}
