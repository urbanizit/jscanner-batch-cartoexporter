package org.urbanizit.jscanner.batch.exporter.dto;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class ComponentDescriptor{
	
	private String name;
	private String filename;
	private String project;
	private String domain;
	private String componentName;
	private String type;
	
	private Set<ComponentDependency> dependencies = new HashSet<ComponentDependency>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public Set<ComponentDependency> getDependencies() {
		return dependencies;
	}
	public void setDependencies(Set<ComponentDependency> dependencies) {
		this.dependencies = dependencies;
	}	
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	
	public void addDependencie(ComponentDependency dependency){
		if(dependency == null){
			throw new IllegalArgumentException("Dependency is null");
		}
		this.dependencies.add(dependency);
	}
		
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		.append(name)
		.append(filename)
		.append(project)
		.append(domain)
		.append(type)
		.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ComponentDescriptor))
			return false;
		ComponentDescriptor other = (ComponentDescriptor)obj;
					
		return new EqualsBuilder()
			.append(name, other.getName())
			.append(filename, other.getFilename())
			.append(project, other.getProject())
			.append(domain, other.getDomain())
			.append(type, other.getType())
			.isEquals();
	} 
}