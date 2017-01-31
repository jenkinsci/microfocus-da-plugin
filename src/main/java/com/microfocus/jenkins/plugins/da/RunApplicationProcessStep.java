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

package com.microfocus.jenkins.plugins.da;

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.microfocus.jenkins.plugins.da.client.DAClient;
import com.microfocus.jenkins.plugins.da.model.DAStep;
import com.microfocus.jenkins.plugins.da.utils.DAUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.console.HyperlinkNote;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.Date;

/**
 * Step to run an application process in Micro Focus Deployment Automation
 *
 * @author Kevin A. Lee
 */
public class RunApplicationProcessStep extends DAStep {

    private String application;
    private String environment;
    private String process;
    private String versions;
    private String properties;
    private boolean onlyChangedVersions;

    @DataBoundSetter
    public void setApplication(final String application) {
        this.application = application;
    }

    public String getApplication() {
        return this.application;
    }

    @DataBoundSetter
    public void setEnvironment(final String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return this.environment;
    }

    @DataBoundSetter
    public void setProcess(final String process) {
        this.process = process;
    }

    public String getProcess() {
        return this.process;
    }

    @DataBoundSetter
    public void setVersions(final String versions) {
        this.versions = versions;
    }

    public String getVersions() {
        return this.versions;
    }

    @DataBoundSetter
    public void setProperties(final String properties) {
        this.properties = properties;
    }

    public String getProperties() {
        return this.properties;
    }

    @DataBoundSetter
    public void setOnlyChangedVersions(final boolean onlyChangedVersions) {
        this.onlyChangedVersions = onlyChangedVersions;
    }

    public boolean getOnlyChangedVersions() {
        return this.onlyChangedVersions;
    }

    @DataBoundConstructor
    public RunApplicationProcessStep(final String url) {
        this.setUrl(DAUtils.rmSlashFromUrl(url));
    }


    /**
     * Descriptor for {@link RunApplicationProcessStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See {@code src/main/resources/com/microfocus/jenkins/plugins/da/CreateVersionStep/*.jelly}
     * for the actual HTML fragment for the configuration screen.
     * </p>
     */
    @Symbol("daRunApplicationProcess")
    @Extension
    public static class DescriptorImpl extends DADescriptorImpl {

        private FormValidation verifyApplication(final String application) {
            if (StringUtils.isEmpty(application))
                return FormValidation.error("An application is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckApplication(@QueryParameter final String value) {
            return verifyApplication(value);
        }

        private FormValidation verifyEnvironment(final String environment) {
            if (StringUtils.isEmpty(environment))
                return FormValidation.error("An environment is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckEnvironment(@QueryParameter final String value) {
            return verifyEnvironment(value);
        }

        private FormValidation verifyProcess(final String process) {
            if (StringUtils.isEmpty(process))
                return FormValidation.error("A process is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckProcess(@QueryParameter final String value) {
            return verifyProcess(value);
        }

        private FormValidation verifyVersions(final String versions) {
            if (StringUtils.isEmpty(versions))
                return FormValidation.error("One or more versions are required");
            return FormValidation.ok();
        }

        public FormValidation doCheckVersions(@QueryParameter final String value) {
            return verifyVersions(value);
        }

        @Override
        public String getDisplayName() {
            return "Micro Focus DA Run Application Process";
        }
   }

    @Override
    public void perform(Run<?,?> run, FilePath workspace, Launcher launcher, TaskListener listener)
        throws InterruptedException, IOException {

        this.setLogger(listener.getLogger());

        String resolvedApplicationName = run.getEnvironment(listener).expand(getApplication());
        String resolvedEnvironmentName = run.getEnvironment(listener).expand(getEnvironment());
        String resolvedProcessName = run.getEnvironment(listener).expand(getProcess());
        String resolvedDeployVersions = run.getEnvironment(listener).expand(getVersions());
        String resolvedDeployProperties = run.getEnvironment(listener).expand(getProperties());

        final UsernamePasswordCredentials usernamePasswordCredentials = DAUtils.getUsernamePasswordCredentials(getCredentialsId());
        if (usernamePasswordCredentials == null) {
            throw new AbortException("Micro Focus DA account credentials are not filled in");
        }

        // check connection to Micro Focus DA
        DAClient daClient = new DAClient(this.getUrl(),
                usernamePasswordCredentials.getUsername(),
                usernamePasswordCredentials.getPassword());
        try {
            daClient.verifyConnection();
        } catch (Exception ex) {
            throw new AbortException("Unable to connect to Micro Focus DA: " + ex.toString());
        }

        // check that the application exists
        try {
            daClient.verifyApplicationExists(resolvedApplicationName);
        } catch (Exception ex) {
            throw new AbortException("Unable to find application with name: " + resolvedApplicationName);
        }

        // check that the application environment exists
        try {
            daClient.verifyEnvironmentExists(resolvedEnvironmentName, resolvedApplicationName);
        } catch (Exception ex) {
            throw new AbortException("Unable to find application environment with name: " + resolvedEnvironmentName);
        }

        // check that the application process exists
        try {
            daClient.verifyApplicationProcessExists(resolvedProcessName, resolvedApplicationName);
        } catch (Exception ex) {
            throw new AbortException("Unable to find application process with name: " + resolvedProcessName);
        }

        // create versions properties string
        String versionsProperties = "[";
         log("Using the following deployment versions with process \"" + resolvedProcessName + "\":");
        // iterate over properties
        BufferedReader bufReader = new BufferedReader(new StringReader(resolvedDeployVersions));
        String line = null, propName = null, propVal = null;
        while ((line = bufReader.readLine()) != null) {
            if (line.trim().length() > 0) { // ignore empty lines
                String[] parts = line.trim().split("=");
                log("\t" + parts[0] + " = " + parts[1]);
                versionsProperties += ("{\"component\":\"" + parts[0] + "\", \"version\":\"" + parts[1] + "\"},");
            }
        }
        // remove last comma if it exists
        if (versionsProperties.endsWith(",")) versionsProperties =
                versionsProperties.substring(0, versionsProperties.length() - 1);
        versionsProperties += "]";
        //log("Constructed the following version properties string \"" + versionsProperties + "\":");

        // create deployment properties string
        String deployProperties = "";
        if (resolvedDeployProperties == null || resolvedDeployProperties.trim().length() == 0) {
            log("No deployment properties defined");
        } else {
            deployProperties += "{";
            log("Using the following deployment properties with process \"" + resolvedProcessName + "\":");
            // iterate over properties
            bufReader = new BufferedReader(new StringReader(resolvedDeployProperties));
            while ((line = bufReader.readLine()) != null) {
                if (line.trim().length() > 0) { // ignore empty lines
                    String[] parts = line.trim().split("=");
                    log("\t" + parts[0] + " = " + parts[1]);
                    deployProperties += ("\"" + parts[0] + "\": \"" + parts[1] + "\",");
                }
            }
            // remove last comma if it exists
            if (deployProperties.endsWith(",")) deployProperties =
                    deployProperties.substring(0, deployProperties.length() - 1);
            deployProperties += "}";
        }

        String requestId = null;
        try {
            log("Starting deployment of application \"" + resolvedApplicationName + "\" to environment \"" +
                    resolvedEnvironmentName + "\"");
            URI uri = UriBuilder.fromPath(getUrl()).path("cli").path("applicationProcessRequest")
                    .path("request").build();
            String jsonString =
                    "{\"application\":\"" + resolvedApplicationName +
                            "\",\"applicationProcess\":\"" + resolvedProcessName +
                            "\",\"environment\":\"" + resolvedEnvironmentName +
                            "\",\"properties\":" + deployProperties +
                            ",\"versions\":" + versionsProperties + "}";
            //log("Sending the following application process request: " + jsonString);
            String deployJson = daClient.executeJSONPut(uri,jsonString);
            JSONObject deployObj = new JSONObject(deployJson);
            requestId = deployObj.getString("requestId");
            log("Successfully started deployment process. See the " +
                    HyperlinkNote.encodeTo(getUrl() + "/app#/application-process-request/" +
                            requestId + "/log", "Micro Focus DA logs") +
                    " for more details.");
        } catch (Exception ex) {
            throw new AbortException("Unable to run application process: " + ex.toString());
        }

        String deploymentResult = "SCHEDULED";
        long startTime = new Date().getTime();

        try {
            while (deploymentResult == "EXECUTING" || deploymentResult == "SCHEDULED") {
                Thread.sleep(3000L);
                deploymentResult = daClient.checkProcessStatus(DAClient.PROCESS_TYPE.applicationProcess, requestId);
            }
        } catch (Exception ex) {
            throw new AbortException("Unable to check application process deployment status: " + ex.toString());
        }

        if (deploymentResult != null)
        {
            long duration = (new Date().getTime() - startTime) / 1000L;
            log("The deployment process took " + duration + " seconds");

            log("The deployment \"" + deploymentResult + "\". See the " +
                    HyperlinkNote.encodeTo(getUrl() + "/app#/application-process-request/" +
                        requestId + "/log", "Micro Focus DA logs") +
                    " for more details.");
            if (("faulted".equalsIgnoreCase(deploymentResult)) || ("failed to start".equalsIgnoreCase(deploymentResult))) {
                run.setResult(Result.UNSTABLE);
            }
        }

    }
}

