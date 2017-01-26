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

package com.microfocus.jenkins.plugins.da.model;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials;
import com.cloudbees.plugins.credentials.domains.DomainRequirement;
import com.microfocus.jenkins.plugins.da.client.DAClient;
import com.microfocus.jenkins.plugins.da.utils.DAUtils;
import hudson.AbortException;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic Micro Focus Deployment Automation Step - Base Class
 *
 * For a new step, subclass this class and implement DescriptorImpl class
 * and perform methods.
 *
 * @author Kevin A. Lee
 */
public abstract class DAStep extends Builder implements SimpleBuildStep {

    private String url;
    private String credentialsId;
    private DAClient daClient;
    private PrintStream logger;
    private List<String> fileList = new ArrayList<String>();

    public DAStep() {
        this.url = "http://localhost:8080/da";
    }

    /*
    In the subclass create a constructor similar to the following:

    @DataBoundConstructor
    public DAStep(final String url) {
       this.url = DAUtils.rmSlashFromUrl(url);
    }
    */

    @DataBoundSetter
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    @DataBoundSetter
    public void setCredentialsId(final String credentialsId) {
        this.credentialsId = credentialsId;
    }

    public String getCredentialsId() {
        return this.credentialsId;
    }

    public void setDaClient(DAClient daClient) {
        this.daClient = daClient;
    }

    public DAClient getDaClient() {
        return this.daClient;
    }

    public void setLogger(PrintStream logger) {
        this.logger = logger;
    }

    public PrintStream getLogger() {
        return this.logger;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public List<String> getFileList() {
        return fileList;
    }

    /**
     * Write text to the build's console log (logger), prefixed with
     * "[Micro Focus DA]".
     *
     * @param text   message to log
     */
    public void log(final String text) {
        this.getLogger().println("[Micro Focus DA] " + text);
    }

    /**
     * Resolve any Jenkins variables with their "real" values from the environment.
     *
     * @param input the string containing Jenkins variables
     * @return the "real" values from the environment
     */
    public String resolveVariables(Map<String, String> envMap, String input) {
        String result = input;
        if (input != null && input.trim().length() > 0) {
            Pattern pattern = Pattern.compile("\\$\\{[^}]*}");
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                String key = result.substring(matcher.start() + 2, matcher.end() - 1);
                if (envMap.containsKey(key)) {
                    result = matcher.replaceFirst(Matcher.quoteReplacement(envMap.get(key)));
                    matcher.reset(result);
                }
            }
        }

        return result;
    }

    /*
     *
     * Extend this class as "DescriptorImpl" and include additional fields/validation for the steps
     * - include the @Extension point (commented out below) and @Symbol annotation with the relevant
     * name you want the step to executed as.
     */
    //@Symbol("daStep")
    //@Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public abstract static class DADescriptorImpl extends BuildStepDescriptor<Builder> {
        public DADescriptorImpl() {
            load();
        }

        public ListBoxModel doFillCredentialsIdItems(@AncestorInPath Item item) {
            return new StandardListBoxModel().withEmptySelection().withMatching(
                    CredentialsMatchers.anyOf(
                            CredentialsMatchers.instanceOf(UsernamePasswordCredentials.class)
                    ),
                    CredentialsProvider.lookupCredentials(StandardCredentials.class, item, null, Collections.<DomainRequirement>emptyList())
            );
        }

        private FormValidation verifyUrl(final String url) {
            if (!DAUtils.isUrl(url))
                return FormValidation.error("Not a valid URL");
            return FormValidation.ok();
        }

        public FormValidation doCheckUrl(@QueryParameter final String value) {
            return verifyUrl(value);
        }

        /*
         * Override this method with the steps display name
         */
        @Override
        public String getDisplayName() {
            return "Micro Focus DA Step";
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }

    /*
     * This is an example perform method, replace this in your subclass
     */
    @Override
    public void perform(Run<?,?> run, FilePath workspace, Launcher launcher, TaskListener listener)
        throws InterruptedException, IOException {

        this.setLogger(listener.getLogger());

        final UsernamePasswordCredentials usernamePasswordCredentials = DAUtils.getUsernamePasswordCredentials(getCredentialsId());
        if (usernamePasswordCredentials == null) {
            throw new AbortException("Micro Focus DA account credentials are not filled in.");
        }

        DAClient daClient = new DAClient(this.getUrl(),
                usernamePasswordCredentials.getUsername(),
                usernamePasswordCredentials.getPassword());
        try {
            daClient.verifyConnection();
        } catch (Exception ex) {
            throw new AbortException("Unable to verify connection to Micro Focus DA: " + ex.getLocalizedMessage());
        }

    }
}

