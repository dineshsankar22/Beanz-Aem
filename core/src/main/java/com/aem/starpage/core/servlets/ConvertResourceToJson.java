package com.aem.starpage.core.servlets;


import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonItemWriter;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.TidyJsonItemWriter;

/**
 * Example of how to easily turn a Node into a JSONObject.
 */
public class ConvertResourceToJson {

    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertResourceToJson.class);

    /**
     * Get the JSON representation of a Resource
     *
     * @param resolver Resolver to get resource
     * @param resource Resource to turn into JSON
     * @return JSON representation of the resource
     */
    public JSONObject resourceToJSON( final Node node) {
//        final Node node = resource.adaptTo(Node.class);
        final StringWriter stringWriter = new StringWriter();
        
        TidyJsonItemWriter jsonWriter = new TidyJsonItemWriter(null);

        
        final Set<String> propertiesToIgnore = new HashSet();
		  propertiesToIgnore.add("jcr:lastModifiedBy");
		  propertiesToIgnore.add("jcr:primaryType");
		  propertiesToIgnore.add("cq:lastModifiedBy");
		  propertiesToIgnore.add("jcr:created");
		  propertiesToIgnore.add("jcr:createdBy");
		  propertiesToIgnore.add("cq:template");
		  
		  propertiesToIgnore.add("jcr:lastModified");
		  propertiesToIgnore.add("cq:lastModified");
		  propertiesToIgnore.add("jcr:mixinTypes");
		  propertiesToIgnore.add("jcr:versionHistory");
		  propertiesToIgnore.add("jcr:baseVersion");
		  propertiesToIgnore.add("sling:resourceType");
//		  propertiesToIgnore.add("root.responsivegrid.footer");
//		  propertiesToIgnore.add("jcr:title");
		  jsonWriter = new TidyJsonItemWriter(propertiesToIgnore);
		  
      

        JSONObject jsonObject = null;

        try {
            /* Get JSON with no limit to recursion depth. */
            jsonWriter.dump(node, stringWriter, -1);
            jsonObject = new JSONObject(stringWriter.toString());
        } catch (RepositoryException | JSONException e) {
            LOGGER.error("Could not create JSON", e);
        }

        return jsonObject;
    }
}
