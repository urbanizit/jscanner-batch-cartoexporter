package org.urbanizit.jscanner.batch.cartoexporter.dto;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;


public class ComponentDescriptorWriter{
	
	private File rootPath;
	
	public ComponentDescriptorWriter(File rootPath){
		if(rootPath == null || !rootPath.isDirectory()){
			throw new IllegalArgumentException("Root path is invalid");
		}		
		this.rootPath = rootPath;		
	}
	
	public void write(final Collection<ComponentDescriptor> componentDescriptors){
		if(componentDescriptors == null){
			return;
		}
		
		for (ComponentDescriptor componentDescriptor : componentDescriptors) {
			write(componentDescriptor);
		}		
	}
	
	public void write(final ComponentDescriptor componentDescriptor){
		if(componentDescriptor == null){
			return;
		}
		
		try {
			String projectName =  componentDescriptor.getProject();
		    String componentName = componentDescriptor.getFilename();
		    String domainName = componentDescriptor.getDomain();
		    	
		    File fileDirectory = new File(rootPath.getPath()+ File.separator +domainName  + File.separator + projectName + File.separator );
		    fileDirectory.mkdirs();
		    
		    File fileDescriptor = new File(fileDirectory.getPath()+ File.separator + componentName +".yml");
		    fileDescriptor.createNewFile();
				
				
			FileWriter fileWriter = new FileWriter(fileDescriptor);
			fileWriter.write("name: "+componentDescriptor.getName()+"\n");
			fileWriter.write("filename: "+componentDescriptor.getFilename()+"\n");
			fileWriter.write("type: "+componentDescriptor.getType()+"\n");
			fileWriter.write("domain: "+componentDescriptor.getDomain()+"\n");
			
			if(componentDescriptor.getDependencies() != null && !componentDescriptor.getDependencies() .isEmpty()){
									
				fileWriter.write("relationships: \n");
						
				Collection<ComponentDependency> componentDependencies = componentDescriptor.getDependencies();
				List<ComponentDependency> sortedDependencies = new ArrayList<ComponentDependency>(componentDependencies);
				
				Collections.sort(sortedDependencies, new ComponentDependencyComparator());
				
				for (ComponentDependency dependency : sortedDependencies) {		

					fileWriter.write("- use: "+dependency.getUse()+"\n");	
					fileWriter.write("  type: "+dependency.getType()+" \n");	
					if(dependency.getMethod() != null){
						fileWriter.write("  method: "+dependency.getMethod()+"\n");							
					}
				}							
			}
			fileWriter.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private class ComponentDependencyComparator implements Comparator<ComponentDependency>{

		@Override
		public int compare(ComponentDependency o1, ComponentDependency o2) {
			
			CompareToBuilder builder = new CompareToBuilder();
			builder.append( o1.getType(),   o2.getType());
			builder.append( o1.getUse(),    o2.getUse());
			builder.append( o1.getMethodName(), o2.getMethodName());
			
			return builder.toComparison();
		}
		
		
	}
}