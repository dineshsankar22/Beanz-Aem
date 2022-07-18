package com.aem.starpage.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.wcm.api.WCMMode;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + "GET",
		"sling.servlet.paths=" + "/aempage/bin/api/content/v3",
		"sling.servlet.extensions=" + "json" })
public class RestServlet extends SlingSafeMethodsServlet {

	/**
	 * Default long serial version ID
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(RestServlet.class);

	Resource resource;
	@Reference
	private ResourceResolverFactory resolverFactory;

	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		WCMMode wcmMode = WCMMode.fromRequest(request);

		String nodePath = "/content/starpage/en/" + request.getParameter("page").trim();

		response.setContentType("json");
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "max-age=0");
		JSONObject jsonObject = new JSONObject();

		final PrintWriter out = response.getWriter();
		// final ResourceResolver resolver = request.getResourceResolver();
		// final Resource resource =
		// resolver.getResource("/content/my-app/us/en/my-page/jcr:content");

		ConvertResourceToJson convertToJSON = new ConvertResourceToJson();
		out.write("{");
		try {
			Node node = null;

			resource = request.getResourceResolver().getResource(nodePath);

			if (resource != null) {

				node = resource.adaptTo(Node.class);

				NodeIterator nodeItr = node.getNodes();
				int respLevel = 1;

				// iterate(nodeItr, out, nodePath);
				// out.println("JSON data : "+jsonObject);

				while (nodeItr.hasNext()) {
					Node cNode = nodeItr.nextNode();
					if (cNode.getName().equals("root")) {
						nodeItr = cNode.getNodes();
						continue;

					}
					if (cNode.getName().equals("jcr:content")) {
						nodeItr = cNode.getNodes();
						continue;

					}
					if (cNode.getName().contains("responsivegrid")) {
						if (respLevel == 1) {
							nodeItr = cNode.getNodes();
							respLevel++;
							continue;

						} else if (respLevel > 1) {
							NodeIterator nIte = cNode.getNodes();

							while (nIte.hasNext()) {
								Node newNode = nIte.nextNode();

								if (newNode.getName().contains("cq:responsive")) {

									continue;
								}

								if (nodeItr.hasNext()) {
									out.write("\"" + newNode.getName() + "\"" + ":"
											+ convertToJSON.resourceToJSON(newNode).toString() + ",");
								} else {
									if (nIte.hasNext()) {
										out.write("\"" + newNode.getName() + "\"" + ":"
												+ convertToJSON.resourceToJSON(newNode).toString() + ",");
									} else {
										out.write("\"" + newNode.getName() + "\"" + ":"
												+ convertToJSON.resourceToJSON(newNode).toString());
									}

								}

							}

						}

						continue;
					}

					if (cNode.getName().contains("cq:responsive")) {

						continue;
					}
					if (nodeItr.hasNext()) {
						out.write("\"" + cNode.getName() + "\"" + ":" + convertToJSON.resourceToJSON(cNode).toString()
								+ ",");
					} else {
						out.write("\"" + cNode.getName() + "\"" + ":" + convertToJSON.resourceToJSON(cNode).toString());
					}

				}

				out.write("}");

			} else {
				out.write("{Please provide correct parameters ex: Host:/aempage/bin/api/content/v3?page=home}");
			}
		}

		catch (Exception e) {
			out.print(e);
			out.write("e");
		}
		out.flush();
		out.close();
	}

}
