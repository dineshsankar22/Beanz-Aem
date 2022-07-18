package com.aem.starpage.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.jackrabbit.oak.commons.json.JsonObject;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.day.cq.commons.TidyJsonItemWriter;
import com.day.crx.packaging.JSONResponse;

@Component(service = Servlet.class,
property = {
"sling.servlet.methods=" + "GET",
"sling.servlet.paths=" + "/aempage/bin/api/content/v2",
"sling.servlet.extensions=" + "json"}

)
public class TestRest extends SlingSafeMethodsServlet {

    Resource resource;
    @Reference
    ResourceResolverFactory resolverFactory;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
                
                String param=request.getParameter("page").trim();

       String nodePath="/content/starpage/en/"+param;

       PrintWriter out=response.getWriter();
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");
       response.setHeader("Cache-Control","max-age=0");


       try {
        
        Node node=null;
        JSONArray jArray=new JSONArray();
        resource=request.getResourceResolver().getResource(nodePath);
       if (resource!=null) {

        Resource cont = resource.getChild("jcr:content").getChild("root");
        node=cont.adaptTo(Node.class);
        NodeIterator ite=node.getNodes();

        JSONObject jsonObject=null;
        out.write("{"+"\""+"Response"+"\""+":");
        // out.write("{name:bkhan,job:software}");

        while (ite.hasNext()) {

            final StringWriter stringWriter = new StringWriter();
			
			TidyJsonItemWriter jsonWriter = new TidyJsonItemWriter(null);

            Node cNode=ite.nextNode();
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
			  propertiesToIgnore.add("cq:responsive");
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
				out.write(jsonObject.toString());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        out.println("}");
       
        
       }
        
       
       } catch (Exception e) {
        //TODO: handle exception
        out.write("Errorrrrrr");
       }

    }
}
