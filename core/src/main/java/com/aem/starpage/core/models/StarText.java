package com.aem.starpage.core.models;


	import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
	import org.apache.sling.models.annotations.Model;
	import org.apache.sling.models.annotations.Required;
	import org.apache.sling.models.annotations.injectorspecific.Self;

	import java.util.List;
	import java.util.logging.Logger;

	import javax.annotation.PostConstruct;
	import javax.inject.Inject;
	import javax.inject.Named;

	@Model(adaptables = Resource.class)
	public class StarText {
	    @Inject @Named("title")
	    @Default(values = " ")

	    private String title;

	    public String getTitle() {
	    return title;
	    }
	    
	    @Inject @Named("description")
	    @Default(values = " ")

        private String description;

	    public String getDescription() {
	    return description;
	    }
	    
	    @Inject @Named("fileReference")
	    @Default(values = " ")

	    private String file;

	    public String getFile() {
	    return file;
	    }


	}

