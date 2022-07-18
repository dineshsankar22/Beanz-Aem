package com.aem.starpage.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.jcr.JsonItemWriter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
//import org.json.*;

import com.day.cq.replication.PathNotFoundException;
import com.day.cq.wcm.api.WCMMode;

import com.day.cq.commons.TidyJSONWriter;
import com.day.cq.commons.TidyJsonItemWriter;
/**
 * Called by FED on Rooms and Rates Listing pages for each hotel.
 * 
 * 
 */

@Component(service = Servlet.class,
property = {
"sling.servlet.methods=" + "GET",
"sling.servlet.paths=" + "/aempage/bin/api/content/v1",
"sling.servlet.extensions=" + "json"})
//@Component(immediate = true, metatype = true, label = ServletConstants.LABEL_WYN_PROPERTY_ROOM_IMAGE_SERVLET)
/*@Properties({
		@Property(name = ServletConstants.PN_SLING_SERVLET_PATHS_NAME, value = ServletConstants.PATH_PROPERTY_ROOM_IMAGE_FORAPP),
		@Property(name = ServletConstants.PN_SLING_SERVLET_EXTENSIONS_NAME, value = ServletConstants.EXTENSION_JSON),
		@Property(name = ServletConstants.PN_SLING_SERVLET_METHODS_NAME, value = { HttpConstants.METHOD_GET }) })
*/

public class AlertServletForApp extends SlingAllMethodsServlet {
	/**
	 * Default long serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AlertServletForApp.class);

		
	
	
	Resource resource;
	@Reference
	private ResourceResolverFactory resolverFactory;
	     
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException{
		WCMMode wcmMode = WCMMode.fromRequest(request);
	 
		String nodePath="/content/starpage/en/"+request.getParameter("page").trim();
		
		response.setContentType("json");
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control","max-age=0");
		JSONObject jsonObject = new JSONObject();
		try {
			Node node = null;

			resource = request.getResourceResolver().getResource(nodePath);

			if (resource != null) {
                
				node = resource.adaptTo(Node.class);
				NodeIterator nodeItr = node.getNodes();

				iterate(nodeItr,out,nodePath);
				 

			}
			else
			{
				out.println("{Please provide correct parameters ex: Host:starpage/bin/service.en_us.authenticated.home.mobile}");
			}
		}

		catch (Exception e) {
			out.print(e);
		}
		out.flush();
		out.close();
	}
	@SuppressWarnings("deprecation")
	private void iterate(NodeIterator nodeItr,PrintWriter out,String strpath)
			throws RepositoryException, ValueFormatException, PathNotFoundException, IOException, JSONException {
		JSONObject jsonObject = null;
		//out.write("{");
		int i=1;
		
			while (nodeItr.hasNext()) {
			Node cNode = nodeItr.nextNode();
			//response.println("CNode vallue"+cNode);

			
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
			 
			  jsonWriter = new TidyJsonItemWriter(propertiesToIgnore);
			
		
			try {
				jsonWriter.dump(cNode, stringWriter, -1);
			} catch (org.apache.sling.commons.json.JSONException e) {
				e.printStackTrace();
			}
			try {
				jsonObject = new JSONObject(stringWriter.toString());
				//System.out.println("Hello tag"+jsonObject.toString());
				//System.out.println("Hello tag"+jsonObject.optString("cq:tags"));
				out.println(jsonObject); 
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		
			}
		//out.write("}");
	}
		  
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


	//Method to separate the tags

	
	
	
}

