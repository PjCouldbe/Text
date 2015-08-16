package pretext;

import java.util.HashSet;

public class Concept {
	private String value;
	private HashSet<Concept> refs;
	
	public Concept(String value) {
		this.value = value;
		refs = new HashSet<Concept>();
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public HashSet<Concept> getRefs() {
		return refs;
	}
	
	public void setRefs(HashSet<Concept> refs) {
		this.refs = refs;
	}
}