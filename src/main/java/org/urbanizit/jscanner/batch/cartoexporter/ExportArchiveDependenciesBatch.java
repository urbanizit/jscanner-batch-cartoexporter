package org.urbanizit.jscanner.batch.cartoexporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.urbanizit.jscanner.ArchiveTypes;
import org.urbanizit.jscanner.batch.cartoexporter.dto.ComponentDependency;
import org.urbanizit.jscanner.batch.cartoexporter.dto.ComponentDescriptor;
import org.urbanizit.jscanner.batch.cartoexporter.dto.ComponentDescriptorWriter;
import org.urbanizit.jscanner.transfert.Archive;
import org.urbanizit.jscanner.transfert.ArchiveCriteria;
import org.urbanizit.jscanner.transfert.Method;
import org.urbanizit.jscanner.transfert.NestableArchive;
import org.urbanizit.jscanner.transfert.itf.AnalyseServiceItf;

/**
 * Simple runner to register archives
 * 
 * @author ldassonville
 *
 */
public class ExportArchiveDependenciesBatch{

	private static final Logger LOGGER = LoggerFactory.getLogger(ExportArchiveDependenciesBatch.class);
	
	@Inject private AnalyseServiceItf analyseServiceItf;
		
	private TreeSet<String> componentProjectNametreeSet = new TreeSet<String>();
	private TreeSet<String> projectDomainNametreeSet = new TreeSet<String>();
	
	private Map<String, ComponentDescriptor> componentDescriptors = new HashMap<String, ComponentDescriptor>();

	
	
	
	public static void main(String[] args) {
		ExportArchiveDependenciesBatch batch = new ExportArchiveDependenciesBatch();
		batch.init();
		batch.exportArchive();
		
	}

	public void init(){
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-rmi.xml");
		this.analyseServiceItf = (AnalyseServiceItf)ctx.getBean("RmiAnalyseService");
	}
	

    public void exportArchive(){
    	
    	ArchiveCriteria archiveCriteria =  new ArchiveCriteria();
    	archiveCriteria.setArchiveNames(Arrays.asList("*.war"));
    	archiveCriteria.setArchiveNames(Arrays.asList("*.ear"));
    	
    	
    	
    	try {
    		List<Archive> archives = analyseServiceItf.findArchiveByCriteria(archiveCriteria, true);

    		//Lancement de l'analyse
    		for (Archive archive : archives) {		
    			
    			
    			LOGGER.debug("Exporting archive {}"+archive.getName());
    			loadComponent(archive);
    			writeComponentDescriptor(new File("P://import/"));
			}

    	} catch (Exception e) {
			LOGGER.error("Error exportArchive", e);
		}  
    }    
    
    public ComponentDescriptor loadComponentDescriptor(Archive archive){
    	    	
    	ComponentDescriptor res = new ComponentDescriptor();

    	res.setName(archive.getName());
    	res.setFilename(archive.getName());
    	res.setType(resolveType(archive));
    	res.setDomain(resolveDomain(archive));
    	
    	res.setProject(resolveProject(archive));
    	res.setComponentName(resolveComponentName(archive));

    	return res;
    	
    }
        
    
    public void loadComponent(Archive archive){
    	if(archive == null){
    		return;
    	}
		
    	ComponentDescriptor componentDescriptor;
		String ownerGroup = archive.getOwnerGroup();
		if(componentDescriptors.containsKey(archive.getOwnerGroup())){
			componentDescriptor = componentDescriptors.get(ownerGroup);
		}else{
			componentDescriptor = loadComponentDescriptor(archive);
			componentDescriptors.put(ownerGroup, componentDescriptor);
		}
		  
		
		//Collection<Archive> extendedList = getSubArchives(archive);
		
		try {
			
			List<Archive> serviceProviders = analyseServiceItf.findDependArchives(archive.getId(), null);
			
			if(serviceProviders != null && !serviceProviders.isEmpty()){

				for (Archive serviceProvider : serviceProviders) {	 
					componentDescriptor.addDependencie(new ComponentDependency(	serviceProvider.getOwnerGroup(), "TECHNICAL DEPENDENCY", null, null));
					
					//for (Archive archive : extendedList) {
						List<Method> methodDependencies =analyseServiceItf.getDependencyMethods(serviceProvider.getId(), archive.getId());
						if((methodDependencies != null && !methodDependencies.isEmpty())){
							System.out.println(methodDependencies);
							
							for (Method method : methodDependencies) {							
								componentDescriptor.addDependencie(new ComponentDependency(
										serviceProvider.getOwnerGroup(), 
										(serviceProvider.isWsArtifact() ? "WS":"EJB"), 
										method.getMethodReadableSignature(),
										method.getMethodName()));
							}	
						}
					//}	
				}	
			}
			
		} catch (Exception e) {
			LOGGER.error("Error loadComponent", e);
		}		
    }
    
    public List<Archive> getSubArchives(Archive archive){
    	
    	List<Archive> res = new ArrayList<Archive>();
    	res.add(archive);
    	if(archive instanceof NestableArchive){ 

    		List<Archive> subArchives = ((NestableArchive)archive).getSubArchives();
    		for (Archive subArchive : subArchives) {
				if(subArchive.getOwnerGroup() != null && subArchive.getOwnerGroup().equalsIgnoreCase(archive.getOwnerGroup())){
					res.add(archive);
				}
			}
    	}
    	return res;
    }
    
    
    
    public void writeComponentDescriptor(File rootDirectory)throws Exception{
    	    	
    	try {		
			ComponentDescriptorWriter componentWriter = new ComponentDescriptorWriter(rootDirectory);
			componentWriter.write(componentDescriptors.values());
						
		} catch (Exception e) {
			LOGGER.error("Error writeComponentDescriptor", e);
			throw e;
		}    	
    }
   
    
    
    //TODO use property
    public String resolveType(Archive archive){
    	if(archive == null){
    		return null;
    	}
    	int typeArchives = ArchiveTypes.getArchiveType(archive.getName());
    	switch (typeArchives) {
		case 0:
			return "WAR";
		case 1:
			return "JAR";
		case 2:
			return "EAR";
		default:
			return "UNKNOW";
		}
    }
   
    
    public String resolveComponentName(Archive archive){
    	if(archive == null){
    		return null;
    	}
    	return FilenameUtils.getBaseName(archive.getName());
    }
    
    public String resolveProject(Archive archive){
    	
    	if(archive == null){
    		return null;
    	}
    	String projectName = null;
    	try{
    		ResourceBundle bundle = ResourceBundle.getBundle("projects-names");
    		projectName = bundle.getString("component."+FilenameUtils.getBaseName(archive.getName())+".project.name");
    		
    		componentProjectNametreeSet.add("component."+FilenameUtils.getBaseName(archive.getName())+".project.name=");

    	}catch (Exception e) {
    		LOGGER.error("Error while resolving project name",e);
		}
    	
    	if(projectName != null){
    		return projectName;
    	}    	
    	return "undefined-project";
    }
    
    public String resolveDomain(Archive archive){
    	if(archive == null){
    		return null;
    	}
    	String projectName = null;
    	try{
    		   		
    		String project = resolveProject(archive);
    		projectDomainNametreeSet.add("project."+project+".domain.name=");
    		
    		ResourceBundle bundle = ResourceBundle.getBundle("domains-names");
    		projectName = bundle.getString("project."+project+".domain.name");
    		    		

    	}catch (Exception e) {
		}
    	
    	if(projectName != null){
    		return projectName;
    	}    	
    	return "undefined-domain";
    }  
}
