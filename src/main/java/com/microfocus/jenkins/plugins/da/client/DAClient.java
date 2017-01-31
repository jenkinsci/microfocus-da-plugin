package com.microfocus.jenkins.plugins.da.client;

/* ===========================================================================
 *  Copyright (c) 2017 Micro Focus. All rights reserved.
 *
 *  Use of the Sample Code provided by Micro Focus is governed by the following
 *  terms and conditions. By using the Sample Code, you agree to be bound by
 *  the terms contained herein. If you do not agree to the terms herein, do
 *  not install, copy, or use the Sample Code.
 *
 *  1.  GRANT OF LICENSE.  Subject to the terms and conditions herein, you
 *  shall have the nonexclusive, nontransferable right to use the Sample Code
 *  for the sole purpose of developing applications for use solely with the
 *  Micro Focus software product(s) that you have licensed separately from Micro Focus.
 *  Such applications shall be for your internal use only.  You further agree
 *  that you will not: (a) sell, market, or distribute any copies of the
 *  Sample Code or any derivatives or components thereof; (b) use the Sample
 *  Code or any derivatives thereof for any commercial purpose; or (c) assign
 *  or transfer rights to the Sample Code or any derivatives thereof.
 *
 *  2.  DISCLAIMER OF WARRANTIES.  TO THE MAXIMUM EXTENT PERMITTED BY
 *  APPLICABLE LAW, SERENA PROVIDES THE SAMPLE CODE AS IS AND WITH ALL
 *  FAULTS, AND HEREBY DISCLAIMS ALL WARRANTIES AND CONDITIONS, EITHER
 *  EXPRESSED, IMPLIED OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 *  IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY, OF FITNESS FOR A
 *  PARTICULAR PURPOSE, OF LACK OF VIRUSES, OF RESULTS, AND OF LACK OF
 *  NEGLIGENCE OR LACK OF WORKMANLIKE EFFORT, CONDITION OF TITLE, QUIET
 *  ENJOYMENT, OR NON-INFRINGEMENT.  THE ENTIRE RISK AS TO THE QUALITY OF
 *  OR ARISING OUT OF USE OR PERFORMANCE OF THE SAMPLE CODE, IF ANY,
 *  REMAINS WITH YOU.
 *
 *  3.  EXCLUSION OF DAMAGES.  TO THE MAXIMUM EXTENT PERMITTED BY APPLICABLE
 *  LAW, YOU AGREE THAT IN CONSIDERATION FOR RECEIVING THE SAMPLE CODE AT NO
 *  CHARGE TO YOU, SERENA SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER,
 *  INCLUDING BUT NOT LIMITED TO DIRECT, SPECIAL, INCIDENTAL, INDIRECT, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, DAMAGES FOR LOSS OF
 *  PROFITS OR CONFIDENTIAL OR OTHER INFORMATION, FOR BUSINESS INTERRUPTION,
 *  FOR PERSONAL INJURY, FOR LOSS OF PRIVACY, FOR NEGLIGENCE, AND FOR ANY
 *  OTHER LOSS WHATSOEVER) ARISING OUT OF OR IN ANY WAY RELATED TO THE USE
 *  OF OR INABILITY TO USE THE SAMPLE CODE, EVEN IN THE EVENT OF THE FAULT,
 *  TORT (INCLUDING NEGLIGENCE), STRICT LIABILITY, OR BREACH OF CONTRACT,
 *  EVEN IF SERENA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.  THE
 *  FOREGOING LIMITATIONS, EXCLUSIONS AND DISCLAIMERS SHALL APPLY TO THE
 *  MAXIMUM EXTENT PERMITTED BY APPLICABLE LAW.  NOTWITHSTANDING THE ABOVE,
 *  IN NO EVENT SHALL SERENA'S LIABILITY UNDER THIS AGREEMENT OR WITH RESPECT
 *  TO YOUR USE OF THE SAMPLE CODE AND DERIVATIVES THEREOF EXCEED US$10.00.
 *
 *  4.  INDEMNIFICATION. You hereby agree to defend, indemnify and hold
 *  harmless Micro Focus from and against any and all liability, loss or claim
 *  arising from this agreement or from (i) your license of, use of or
 *  reliance upon the Sample Code or any related documentation or materials,
 *  or (ii) your development, use or reliance upon any application or
 *  derivative work created from the Sample Code.
 *
 *  5.  TERMINATION OF THE LICENSE.  This agreement and the underlying
 *  license granted hereby shall terminate if and when your license to the
 *  applicable Micro Focus software product terminates or if you breach any terms
 *  and conditions of this agreement.
 *
 *  6.  CONFIDENTIALITY.  The Sample Code and all information relating to the
 *  Sample Code (collectively "Confidential Information") are the
 *  confidential information of Micro Focus.  You agree to maintain the
 *  Confidential Information in strict confidence for Micro Focus.  You agree not
 *  to disclose or duplicate, nor allow to be disclosed or duplicated, any
 *  Confidential Information, in whole or in part, except as permitted in
 *  this Agreement.  You shall take all reasonable steps necessary to ensure
 *  that the Confidential Information is not made available or disclosed by
 *  you or by your employees to any other person, firm, or corporation.  You
 *  agree that all authorized persons having access to the Confidential
 *  Information shall observe and perform under this nondisclosure covenant.
 *  You agree to immediately notify Micro Focus of any unauthorized access to or
 *  possession of the Confidential Information.
 *
 *  7.  AFFILIATES.  Micro Focus as used herein shall refer to Micro Focus,
 *  and its affiliates.  An entity shall be considered to be an
 *  affiliate of Micro Focus if it is an entity that controls, is controlled by,
 *  or is under common control with Micro Focus.
 *
 *  8.  GENERAL.  Title and full ownership rights to the Sample Code,
 *  including any derivative works shall remain with Micro Focus.  If a court of
 *  competent jurisdiction holds any provision of this agreement illegal or
 *  otherwise unenforceable, that provision shall be severed and the
 *  remainder of the agreement shall remain in full force and effect.
 * ===========================================================================
 */

import com.microfocus.jenkins.plugins.da.exceptions.AuthenticationException;
import com.microfocus.jenkins.plugins.da.exceptions.VersionNotExistsException;
import com.urbancode.commons.util.https.OpenSSLProtocolSocketFactory;
import hudson.util.Secret;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.io.Serializable;
import java.net.URI;

/**
 * Provides a generic client and access methods for Micro Focus Deployment Automation
 *
 * @author Kevin A. Lee
 */
public class DAClient implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum PROCESS_TYPE { applicationProcess, componentProcess, genericProcess }

    /**
     * The url.
     */
    private String url;

    /**
     * The username.
     */
    private String user;

    /**
     * The password.
     */
    private Secret password;

    public DAClient() {

    }

    /**
     * Instantiates a new Micro Focus DA Client.
     *
     * @param url         the url of the DA instance
     * @param user        the username
     * @param password    the password
     */
    public DAClient(String url, String user, Secret password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Gets the url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url the new url
     */
    public void setUrl(String url) {
        this.url = url;
        if (this.url != null) {
            this.url = this.url.replaceAll("\\\\", "/");
        }
        while (this.url != null && this.url.endsWith("/")) {
            this.url = this.url.substring(0, this.url.length() - 1);
        }
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUser() {
        return user;
    }

    /**
     * Sets the username.
     *
     * @param user the new username
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets the password.
     *
     * @return the password
     */
    public Secret getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password the new password
     */
    public void setPassword(Secret password) {
        this.password = password;
    }

    public void verifyConnection() throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("state").build();
        executeJSONGet(uri);
    }

    public void verifyComponentExists(String componentName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("component").path("info").queryParam("component", componentName).build();
        executeJSONGet(uri);
    }

    public String getComponentVersionId(String componentId, String versionName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("deploy").path("component").path(componentId)
                .path("versionsPaged").queryParam("inactive", "true").queryParam("searchQuery", versionName)
                .build();
        String versionsJson = executeJSONGet(uri);
        JSONObject versionsObj = new JSONObject(versionsJson);
        int totalRecords = versionsObj.getInt("totalRecords");
        if (totalRecords > 0) {
            JSONArray verArray = versionsObj.getJSONArray("records");
            JSONObject verObj = verArray.getJSONObject(0);
            return verObj.getString("id");
        } else {
            throw new VersionNotExistsException("Version \"" + versionName + "\" does not exist");
        }
    }

    public void verifyApplicationExists(String applicationName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("application").path("info").queryParam("application", applicationName).build();
        executeJSONGet(uri);
    }

    public void verifyEnvironmentExists(String environmentName, String applicationName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("environment").path("info").queryParam("environment", environmentName).queryParam("application", applicationName).build();
        executeJSONGet(uri);
    }

    public void verifyApplicationProcessExists(String applicationProcess, String applicationName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("applicationProcess").path("info").queryParam("application", applicationName).queryParam("applicationProcess", applicationProcess).build();
        executeJSONGet(uri);
    }

    // TODO: this is an expensive operation, we need a new cli class for statuses or rest query by status name
    public void verifyStatusExists(String stateName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("deploy").path("status").path("versionStatuses").build();
        String jsonOut = executeJSONGet(uri);
        if (!jsonOut.contains(stateName)) throw new Exception(stateName + " not found");
    }

    public void verifyResourceExists(String resourceName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("resource").path("info").queryParam("resource", resourceName).build();
        executeJSONGet(uri);
    }

    public String getResourceId(String resourceName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("cli").path("resource").path("info").queryParam("resource", resourceName).build();
        String resourceJson = executeJSONGet(uri);
        JSONObject resourceObj = new JSONObject(resourceJson);
        return resourceObj.getString("id");
    }

    public void verifyProcessExists(String processName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("process").path("false").build();
        String processContent = executeJSONGet(uri);
        String processStr = "\"name\":\"" + processName + "\"";
        if (!processContent.contains(processStr)) throw new Exception(processName + " not found");
    }

    // TODO: this is an expensive operation, we need a new cli class for generic processes or rest query by process name
    public String getProcessId(String processName) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("process").path("false").build();
        String processContent = executeJSONGet(uri);
        String processStr = "\"name\":\"" + processName + "\"";
        int start = processContent.indexOf("\"name\":\"" + processName + "\"");
        if (processContent.contains(processStr)) {
            String processId = processContent.substring(start - 38, start - 2);
            return processId;
        }
        return null;
    }

    public String[] getComponentIdAndRepositoryId(String componentName) throws Exception {
        String[] results = new String[2];
        URI uri = UriBuilder.fromPath(url).path("rest").path("deploy").path("component").path(componentName)
                .build();

        String componentContent = executeJSONGet(uri);

        String componentId = new JSONObject(componentContent).getString("id");
        results[0] = componentId;

        JSONArray properties = new JSONObject(componentContent).getJSONArray("properties");
        if (properties != null) {
            for (int i = 0; i < properties.length(); i++) {
                JSONObject propertyJson = properties.getJSONObject(i);
                String propName = propertyJson.getString("name");
                String propValue = propertyJson.getString("value");

                if ("code_station/repository".equalsIgnoreCase(propName)) {
                    results[1] = propValue.trim();
                    break;
                }
            }
        }
        return results;
    }

    public String getComponentVersionPropsheetId(String verId) throws Exception {
        URI uri = UriBuilder.fromPath(url).path("rest").path("deploy").path("version")
                .path(verId).build();
        String result = null;

        String versionContent = executeJSONGet(uri);

        JSONArray propSheets = new JSONObject(versionContent).getJSONArray("propSheets");
        if (propSheets != null) {
            JSONObject propertyJson = propSheets.getJSONObject(0);
            result = propertyJson.getString("id").trim();
        }
        return result;
    }

    public String checkProcessStatus(PROCESS_TYPE processType, String requestId) throws Exception {
        String typePath = "process";
        String processPath = "request";
        if (processType == PROCESS_TYPE.applicationProcess) {
            typePath = "deploy";
            processPath = "applicationProcessRequest";
        } else if (processType == PROCESS_TYPE.componentProcess) {
            typePath = "deploy";
            processPath = "componentProcessRequest";
        }
        String deploymentResult = "SCHEDULED";

        URI uri = UriBuilder.fromPath(getUrl()).path("rest").path(typePath).path(processPath).path(requestId).build();
        String requestResult = executeJSONGet(uri);
        if (requestResult != null && requestResult.contains("rootTrace"))
        {
            JSONObject rootTrace = new JSONObject(requestResult).optJSONObject("rootTrace");
            if (rootTrace != null) {
                String executionStatus = (String) rootTrace.get("state");
                String executionResult = (String) rootTrace.get("result");
                if ((executionStatus == null) || ("".equals(executionStatus))) {
                    deploymentResult = "FAULTED";
                } else if ("executing".equalsIgnoreCase(executionStatus)) {
                    deploymentResult = "EXECUTING";
                } else if (("closed".equalsIgnoreCase(executionStatus)) || ("faulted".equalsIgnoreCase(executionStatus)) || ("faulted".equalsIgnoreCase(executionResult))) {
                    deploymentResult = executionResult;
                }
            }
        }
        return deploymentResult;
    }

    public String checkGenericProcessStatus(String requestId) throws Exception
    {
        String deploymentResult = "SCHEDULED";

        URI uri = UriBuilder.fromPath(getUrl()).path("rest").path("process").path("request").path(requestId).build();
        String requestResult = executeJSONGet(uri);
        if (requestResult != null)
        {
            JSONObject trace = new JSONObject(requestResult).optJSONObject("trace");
            if (trace != null) {
                String executionStatus = (String) trace.get("state");
                String executionResult = (String) trace.get("result");
                if ((executionStatus == null) || ("".equals(executionStatus))) {
                    deploymentResult = "FAULTED";
                } else if (("closed".equalsIgnoreCase(executionStatus)) || ("faulted".equalsIgnoreCase(executionStatus)) || ("faulted".equalsIgnoreCase(executionResult))) {
                    deploymentResult = executionResult;
                }
            }
        }
        return deploymentResult;
    }

    //
    // generic DA HTTP REST get,post,put methods
    //

    public String executeJSONGet(URI uri) throws Exception {
        String result = null;
        HttpClient httpClient = new HttpClient();

        if ("https".equalsIgnoreCase(uri.getScheme())) {
            ProtocolSocketFactory socketFactory = new OpenSSLProtocolSocketFactory();
            Protocol https = new Protocol("https", socketFactory, 443);
            Protocol.registerProtocol("https", https);
        }

        GetMethod method = new GetMethod(uri.toString());
        setDirectSsoInteractionHeader(method);
        try {
            HttpClientParams params = httpClient.getParams();
            params.setAuthenticationPreemptive(true);

            UsernamePasswordCredentials clientCredentials = new UsernamePasswordCredentials(user, password.getPlainText());
            httpClient.getState().setCredentials(AuthScope.ANY, clientCredentials);

            int responseCode = httpClient.executeMethod(method);
            //if (responseCode < 200 || responseCode < 300) {
            if (responseCode == 401) {
                throw new AuthenticationException("Error connecting to Micro Focus DA: Invalid user and/or password");
            } else if (responseCode != 200) {
                throw new Exception("Error connecting to Micro Focus DA: " + responseCode);
            } else {
                result = method.getResponseBodyAsString();
            }
        } finally {
            method.releaseConnection();
        }

        return result;
    }

    public String executeJSONPut(URI uri, String putContents) throws Exception {
        String result = null;
        HttpClient httpClient = new HttpClient();

        if ("https".equalsIgnoreCase(uri.getScheme())) {
            ProtocolSocketFactory socketFactory = new OpenSSLProtocolSocketFactory();
            Protocol https = new Protocol("https", socketFactory, 443);
            Protocol.registerProtocol("https", https);
        }

        PutMethod method = new PutMethod(uri.toString());
        setDirectSsoInteractionHeader(method);
        if (putContents != null)
            method.setRequestBody(putContents);
        method.setRequestHeader("Content-Type", "application/json");
        method.setRequestHeader("charset", "utf-8");
        try {
            HttpClientParams params = httpClient.getParams();
            params.setAuthenticationPreemptive(true);

            UsernamePasswordCredentials clientCredentials = new UsernamePasswordCredentials(user, password.getPlainText());
            httpClient.getState().setCredentials(AuthScope.ANY, clientCredentials);

            int responseCode = httpClient.executeMethod(method);

            //if (responseCode < 200 || responseCode < 300) {
            if (responseCode == 401) {
                throw new AuthenticationException("Error connecting to Micro Focus DA: Invalid user and/or password");
            } else if (responseCode != 200 && responseCode != 204) {
                throw new Exception("Micro Focus DA returned error code: " + responseCode);
            } else {
                result = method.getResponseBodyAsString();
            }
        } catch (Exception ex) {
            throw new Exception("Error connecting to Micro Focus DA: " + ex.getMessage());
        } finally {
            method.releaseConnection();
        }

        return result;
    }

    public String executeJSONPost(URI uri, String postContents) throws Exception {
        String result = null;
        HttpClient httpClient = new HttpClient();

        if ("https".equalsIgnoreCase(uri.getScheme())) {
            ProtocolSocketFactory socketFactory = new OpenSSLProtocolSocketFactory();
            Protocol https = new Protocol("https", socketFactory, 443);
            Protocol.registerProtocol("https", https);
        }

        PostMethod method = new PostMethod(uri.toString());
        setDirectSsoInteractionHeader(method);
        if (postContents != null)
            method.setRequestBody(postContents);
        method.setRequestHeader("Content-Type", "application/json");
        method.setRequestHeader("charset", "utf-8");
        try {
            HttpClientParams params = httpClient.getParams();
            params.setAuthenticationPreemptive(true);

            UsernamePasswordCredentials clientCredentials = new UsernamePasswordCredentials(user, password.getPlainText());
            httpClient.getState().setCredentials(AuthScope.ANY, clientCredentials);

            int responseCode = httpClient.executeMethod(method);

            if (responseCode == 401) {
                throw new AuthenticationException("Error connecting to Micro Focus DA: Invalid user and/or password");
            } else if (responseCode != 200) {
                throw new Exception("Micro Focus DA returned error code: " + responseCode);
            } else {
                result = method.getResponseBodyAsString();
            }
        } catch (Exception e) {
            throw new Exception("Error connecting to Micro Focus DA: " + e.getMessage());
        } finally {
            method.releaseConnection();
        }

        return result;
    }

    //
    // private methods
    //

    private String encodePath(String path) {
        String result;
        URI uri;
        try {
            uri = new URI(null, null, path, null);
            result = uri.toASCIIString();
        } catch (Exception e) {
            result = path;
        }
        return result;
    }

    private void setDirectSsoInteractionHeader(HttpMethodBase method) {
        method.setRequestHeader("DirectSsoInteraction", "true");
    }

}