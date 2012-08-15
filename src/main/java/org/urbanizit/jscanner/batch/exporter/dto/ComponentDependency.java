package org.urbanizit.jscanner.batch.exporter.dto;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ComponentDependency{
	
	private String use;
	private String type;
	private String method;
	private String methodName;
	
	public ComponentDependency(String use, String type, String method, String methodName){
		this.use = use;
		this.method = method;
		this.type = type;
		this.methodName = methodName;
	}

	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(use)
		.append(type)
		.append(method)
		.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ComponentDependency))
			return false;
		ComponentDependency other = (ComponentDependency)obj;
					
		return new EqualsBuilder()
			.append(use, other.getUse())
			.append(type, other.getType())
			.append(method, other.getMethod())
			.isEquals();
	} 	    	
}