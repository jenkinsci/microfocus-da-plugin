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
import com.microfocus.jenkins.plugins.da.exceptions.VersionNotExistsException;
import com.microfocus.jenkins.plugins.da.model.DAStep;
import com.microfocus.jenkins.plugins.da.utils.DAUtils;
import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

/**
 * Step to add a status to a component version in Micro Focus Deployment Automation
 *
 * @author Kevin A. Lee
 */
public class AddStatusToVersionStep extends DAStep {

    private String component;
    private String version;
    private String status;

    @DataBoundSetter
    public void setComponent(final String component) {
        this.component = component;
    }

    public String getComponent() {
        return this.component;
    }

    @DataBoundSetter
    public void setVersion(final String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    @DataBoundSetter
    public void setStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    @DataBoundConstructor
    public AddStatusToVersionStep(final String url) {
        this.setUrl(DAUtils.rmSlashFromUrl(url));
    }

    /**
     * Descriptor for {@link AddStatusToVersionStep}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See {@code src/main/resources/com/microfocus/jenkins/plugins/da/AddFilesToVersionStep/*.jelly}
     * for the actual HTML fragment for the configuration screen.
     */
    @Symbol("daAddStatusToVersion")
    @Extension
    public static class DescriptorImpl extends DADescriptorImpl {

        private FormValidation verifyComponent(final String component) {
            if (StringUtils.isEmpty(component))
                return FormValidation.error("A component is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckComponentName(@QueryParameter final String value) {
            return verifyComponent(value);
        }

        private FormValidation verifyVersionName(final String version) {
            if (StringUtils.isEmpty(version))
                return FormValidation.error("A version is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckVersion(@QueryParameter final String value) {
            return verifyVersionName(value);
        }

        private FormValidation verifyStatus(final String status) {
            if (StringUtils.isEmpty(status))
                return FormValidation.error("A status is required");
            return FormValidation.ok();
        }

        public FormValidation doCheckStatus(@QueryParameter final String value) {
            return verifyStatus(value);
        }

        public String getDefaultVersion() {
            return "${BUILD_NUMBER}";
        }

        @Override
        public String getDisplayName() {
            return "Micro Focus DA Add Status To Version";
        }
    }

    @Override
    public void perform(Run<?,?> run, FilePath workspace, Launcher launcher, TaskListener listener)
        throws InterruptedException, IOException {

        this.setLogger(listener.getLogger());

        String resolvedComponentName = run.getEnvironment(listener).expand(getComponent());
        String resolvedVersionName = run.getEnvironment(listener).expand(getVersion());
        String resolvedStatusName = run.getEnvironment(listener).expand(getStatus());

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
            throw new AbortException("Unable to connect to Micro Focus DA: " + ex.getLocalizedMessage());
        }

        // check that the component exists
        try {
            daClient.verifyComponentExists(resolvedComponentName);
        } catch (Exception ex) {
            throw new AbortException("Unable to find component with name: " + resolvedComponentName);
        }

        String componentId = null;
        String componentRepositoryId = null;
        String versionId = null;

        // get the internal id of the component
        try {
            // get componentId
            String compId[] = daClient.getComponentIdAndRepositoryId(resolvedComponentName);
            componentId = compId[0];
            componentRepositoryId = compId[1];
            //log("Found component id: " + componentId);
        } catch (Exception ex) {
            throw new AbortException("Unable to retrieve component id: " + ex.toString());
        }

        // check if version exists
        try {
            versionId = daClient.getComponentVersionId(componentId, resolvedVersionName);
            //log("Version \"" + resolvedVersionName + "\" already exists with id: " + versionId);
        } catch (VersionNotExistsException ex) {
            throw new AbortException("\"Version \"" + resolvedVersionName + "\" does not exists");
        } catch (Exception ex) {
            throw new AbortException("Unable to check if version already exists: " + ex.toString());
        }

        try {
            UriBuilder uriBuilder = UriBuilder.fromPath(getUrl()).path("rest").path("deploy").path("version")
                    .path(versionId).path("status").path(resolvedStatusName);
            URI uri = uriBuilder.build();
            String json = "{\"status\":\"" + status + "\"}";
            log("Adding status \"" + resolvedStatusName + "\" to component version id: \"" + versionId + "\"");
            daClient.executeJSONPut(uri, json);
            log("Successfully added status \"" + resolvedStatusName + "\"");
        }
        catch (Throwable ex) {
            throw new AbortException("Failed to add status to component version: " + ex.toString());
        }

    }
}

