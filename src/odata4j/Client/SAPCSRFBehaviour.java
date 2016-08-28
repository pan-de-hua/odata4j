package odata4j.Client;

import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import org.odata4j.consumer.ODataClientRequest;
import org.odata4j.jersey.consumer.behaviors.JerseyClientBehavior;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.api.client.filter.Filterable;

public class SAPCSRFBehaviour implements JerseyClientBehavior {
	private static final String CSRF_HEADER = "X-CSRF-Token";
	private static final String SAP_COOKIES = "SAP_SESSIONID";
	private String xsrfCookieName;
	private String xsrfCookieValue;
	private String xsrfTokenValue;

	@Override
	public ODataClientRequest transform(ODataClientRequest request) {
		if (request.getMethod().equals("GET")) {
			request = request.header(CSRF_HEADER, "Fetch");
			return request;
		} else {
			return request.header(CSRF_HEADER, xsrfTokenValue).header("Cookie", xsrfCookieName + "=" + xsrfCookieValue);
		}
	}

	@Override
	public void modify(ClientConfig arg0) {
	}

	@Override
	public void modifyClientFilters(Filterable client) {
		client.addFilter(new ClientFilter() {
			@Override
			public ClientResponse handle(final ClientRequest clientRequest) throws ClientHandlerException {
				ClientResponse response = getNext().handle(clientRequest);
				List<NewCookie> cookies = response.getCookies();
				for (NewCookie cookie : cookies) {
					if (cookie.getName().startsWith(SAP_COOKIES)) {
						xsrfCookieName = cookie.getName();
						xsrfCookieValue = cookie.getValue();
						break;
					}
				}
				MultivaluedMap<String, String> responseHeaders = response.getHeaders();
				xsrfTokenValue = responseHeaders.getFirst(CSRF_HEADER);
				return response;
			}
		});
	}

	@Override
	public void modifyWebResourceFilters(Filterable arg0) {

	}

}
