package com.aem.starpage.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Value;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = Servlet.class, property = { "sling.servlet.methods=" + "GET",
		"sling.servlet.paths=" + "/bin/querybuilder4tags", "sling.servlet.extensions=" + "json" })
public class Querybuilder4Tags extends SlingAllMethodsServlet {

	
	private static final long serialVersionUID = 8756673276514272698L;
	

	private static final Logger LOG = LoggerFactory.getLogger(Querybuilder4Tags.class);

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		try {

			String queryparam = request.getParameter("queryparam");
			
			String query = createQuery(queryparam, response);
			

			if (query == null) {
				
				
				return;
			}
			JSONObject jsobject=new JSONObject();

			ResourceResolver resolver = request.getResourceResolver();
			Iterator<Resource> results = resolver.findResources(query, "JCR-SQL2");

			
			if(!results.hasNext()){
				out.println("ID is not available");
			}
			while (results.hasNext()) {
				Resource result = results.next();
				response.setContentType("json");

				response.setHeader("Cache-Control", "max-age=0");

				// Removing the ending Newline character and carriage return

//				String path = result.getPath().toString();
				Node node = null;
//				Resource resource;
//
//				resource = request.getResourceResolver().getResource(path);
				node = result.adaptTo(Node.class);

				// out.print(node.getProperty("cq:tags").getString());
				Value[] tags = node.getProperty("cq:tags").getValues();
				List<String> tagList=new ArrayList<>();
				for (Value v : tags) {
					tagList.add(v.toString());
				}
				
				jsobject.put("description", node.getProperty("jcr:description").getString());
				
				jsobject.put("tags", tagList);
				
				out.println(jsobject.toString());
				

			}
			out.flush();
			out.close();

		} catch (Exception e) {
			LOG.error("Exception in AppCFServlet " + e.getMessage());
		   out.println(e.toString());
		}
	}

	private String createQuery(String param, SlingHttpServletResponse response) throws IOException {

		StringBuilder query = new StringBuilder(
				// "SELECT [cq:tags] FROM [cq:Page] WHERE (["+param+"])");
				"SELECT * FROM [cq:PageContent] AS nodes WHERE CONTAINS(nodes.[id], '" + param + "')");

		LOG.info("Query: " + query.toString());
		return query.toString();

	}

}
