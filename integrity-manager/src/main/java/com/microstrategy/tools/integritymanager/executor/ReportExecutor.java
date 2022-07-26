package com.microstrategy.tools.integritymanager.executor;

import com.microstrategy.tools.integritymanager.exception.ReportExecutionException;
import com.microstrategy.tools.integritymanager.exception.ReportExecutorInternalException;
import com.microstrategy.tools.integritymanager.model.bo.ExecutionResult;
import com.microstrategy.tools.integritymanager.model.bo.ReportExecutionResult;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ExecutionResultFormat;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstance;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportInstanceStatus;
import com.microstrategy.tools.integritymanager.model.entity.mstr.report.ReportSqlStatement;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportExecutor {

    static Pattern instanceIdPattern = Pattern.compile("\"instanceId\"\\s*:\\s*\"[A-Z0-9]{32}\"");
    static Pattern instanceStatusPattern = Pattern.compile("\"status\"\\s*:\\s*\\d+");
    private String authToken;
    private String cookie;
    private String projectId;
    private String reportId;
    private String libraryUrl;

    private RestTemplate restTemplate = new RestTemplate();

    public static ReportExecutor build() {
        return new ReportExecutor();
    }

    private ReportExecutor() {

    }

    /**
     * Execute a report.
     * @return
     * @throws ReportExecutorInternalException
     * @throws ReportExecutionException
     */
    public String execute() throws ReportExecutorInternalException, ReportExecutionException {
        return execute(60 * 30);
    }

    /**
     * Execute a report.
     * @param responseType specifies the response type of the report execution result
     * @return
     * @throws ReportExecutorInternalException
     * @throws ReportExecutionException
     */
    public <T> ResponseEntity<T> execute(Class<T> responseType) throws ReportExecutorInternalException, ReportExecutionException {
        return execute(60 * 30, responseType);
    }

    /**
     * Execute a report.
     * @param resultFormats specifies the format type of the report execution result
     * @return
     * @throws ReportExecutorInternalException
     * @throws ReportExecutionException
     */
    public ExecutionResult execute(EnumSet resultFormats) throws ReportExecutorInternalException, ReportExecutionException {
        return execute(60 * 30, resultFormats);
    }

    /**
     * Execute the report.
     * @param maxWaitSecond Max seconds to wait before got data
     * @return The data of the report in format of json string
     */
    public String execute(int maxWaitSecond) throws ReportExecutionException, ReportExecutorInternalException {
        String instanceId = createReportInstanceAndAnwserPrompts(maxWaitSecond);

        // Get the report data
        return fetchReportInstanceData(instanceId);
    }

    /**
     * Execute the report.
     * @param maxWaitSecond Max seconds to wait before got data
     * @return The data of the report in format of ResponseEntity<T>
     */
    public <T> ResponseEntity<T> execute(int maxWaitSecond, Class<T> responseType) throws ReportExecutionException, ReportExecutorInternalException {
        String instanceId = createReportInstanceAndAnwserPrompts(maxWaitSecond);

        // Get the report data
        return fetchReportInstanceData(instanceId, responseType);
    }

    /**
     * Execute the report.
     * @param maxWaitSecond Max seconds to wait before got data
     * @return The data of the report in format of ResponseEntity<T>
     */
    public ExecutionResult execute(int maxWaitSecond, EnumSet resultFormats) throws ReportExecutionException, ReportExecutorInternalException {
        String instanceId = createReportInstanceAndAnwserPrompts(maxWaitSecond);

        ReportExecutionResult result = new ReportExecutionResult();
        // Get the report in string
        String report = fetchReportInstanceData(instanceId);
        result.setReport(report);

        if (resultFormats.contains(ExecutionResultFormat.SQL)) {
            ReportSqlStatement sqlStatement = this.fetchReportSqlStatement(instanceId);
            result.setSql(sqlStatement.getSqlStatement());
        }

        return new ExecutionResult().setReportExecutionResult(result);
    }

    private String createReportInstanceAndAnwserPrompts(int maxWaitSecond) throws ReportExecutorInternalException, ReportExecutionException {
        if (!validateParams()) {
            throw new ReportExecutorInternalException("At least one of the following is not set: " +
                    "authToken, cookie, projectId, reportId, libraryUrl");
        }

        // Create the instance
        String instanceId = createReportInstance();

        // Check instance status
        int reportStatus;

        ReportPromptAnswerer reportPromptAnswerer = new ReportPromptAnswerer(this);
        while ((reportStatus = fetchReportInstanceStatus(instanceId))
                != ReportInstanceStatus.REPORT_INSTANCE_STATUS_FINISH && maxWaitSecond-- >= 0) {
            if (reportStatus == ReportInstanceStatus.REPORT_INSTANCE_STATUS_PROMPTED) {
                reportPromptAnswerer.answer(instanceId);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new ReportExecutorInternalException("Interrupted");
            }
        }

        if (maxWaitSecond < 0) {
            throw new ReportExecutionException(
                    String.format("Max wait time (%d)s exceeds when executing the report: %s",
                            maxWaitSecond, this.reportId));
        }
        return instanceId;
    }

    private String createReportInstance() throws ReportExecutionException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(String.format("%s/api/v2/reports/%s/instances?offset=0",
                    this.libraryUrl, this.reportId));
            conn = buildHttpConnection(url, "POST");

            conn.connect();
            String responseBody = getHttpRespondContent(conn);

            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new ReportExecutionException("Fail to create report instance: " +
                        "Rest API returns with HTTP error code: "
                        + conn.getResponseCode() + "\n" + responseBody);
            }

            return getReportInstanceId(responseBody);

        } catch (MalformedURLException e) {
            throw new ReportExecutionException("Invalid URL", e);
        } catch (IOException e) {
            throw new ReportExecutionException("Unable to get the server output.", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private int fetchReportInstanceStatus(String instanceId) throws ReportExecutionException {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(String.format("%s/api/reports/%s/instances/%s/status",
                    this.libraryUrl, this.reportId, instanceId));

            conn = buildHttpConnection(url, "GET");

            conn.connect();
            String responseBody = getHttpRespondContent(conn);

            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new ReportExecutionException("Fail to fetch report instance status: " +
                        "Rest API returns with HTTP error code: "
                        + conn.getResponseCode() + "\n" + responseBody);
            }

            if (responseBody.length() == 0) {
                return ReportInstanceStatus.REPORT_INSTANCE_STATUS_OTHERS;
            }

            return getReportInstanceStatus(responseBody);

        } catch (MalformedURLException e) {
            throw new ReportExecutionException("Invalid URL", e);
        } catch (IOException e) {
            throw new ReportExecutionException("Unable to get the server output.", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private <T> ResponseEntity<T> fetchReportInstanceData(String instanceId, Class<T> responseType) throws ReportExecutionException {
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/v2/reports/%s/instances/%s", this.libraryUrl, this.reportId, instanceId);

        try {
            ResponseEntity<T> reportInstance = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);
            return reportInstance;
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(exception.getLocalizedMessage(), exception);
        }
    }

    private String fetchReportInstanceData(String instanceId) throws ReportExecutionException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(String.format("%s/api/v2/reports/%s/instances/%s",
                    this.libraryUrl, this.reportId, instanceId));
            conn = buildHttpConnection(url, "GET");
            String responseBody = getHttpRespondContent(conn);

            if (conn.getResponseCode() >= HttpURLConnection.HTTP_BAD_REQUEST) {
                throw new ReportExecutionException("Fail to fetch report instance data: " +
                        "Rest API returns with HTTP error code: "
                        + conn.getResponseCode() + "\n" + responseBody);
            }

            return responseBody;
            //return Json2Csv.convert(responseBody);
        } catch (MalformedURLException e) {
            throw new ReportExecutionException("Invalid URL", e);
        } catch (IOException e) {
            throw new ReportExecutionException("Unable to get the server output.", e);
        } catch (Throwable e) {
            throw new ReportExecutionException("Fatal error when execute report", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private ReportSqlStatement fetchReportSqlStatement(String instanceId) throws ReportExecutionException {
        HttpEntity<Map<String, Object>> requestEntity = newMapHttpEntity();

        String url = String.format("%s/api/v2/reports/%s/instances/%s/sqlView", this.libraryUrl, this.reportId, instanceId);

        try {
            ResponseEntity<ReportSqlStatement> sqlStatementResponse = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ReportSqlStatement.class);
            return sqlStatementResponse.getBody();
        }
        catch (RestClientException exception) {
            exception.printStackTrace();
            throw new ReportExecutionException(exception.getLocalizedMessage(), exception);
        }
    }

    private HttpEntity<Map<String, Object>> newMapHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-MSTR-AuthToken", this.authToken);
        headers.add("X-MSTR-ProjectID", this.projectId);
        headers.addAll("Cookie", Arrays.asList(this.cookie));
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "*/*");
        HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(headers);
        return requestEntity;
    }

    protected String getReportInstanceId(String finalStr) throws ReportExecutionException {

        Matcher matcher = this.instanceIdPattern.matcher(finalStr);
        boolean cantFindId = false;
        String instanceId = "";
        if (matcher.find()) {
            String instanceIdKeyValue = matcher.group();
            String [] split = instanceIdKeyValue.split(":");
            if (split.length != 2) {
                cantFindId = true;
            } else {
                instanceId = split[1].replaceAll("\\s*\"\\s*", "");
            }
        }

        if (cantFindId) {
            throw new ReportExecutionException("Fail to get the report instance ID.");
        }

        return instanceId;
    }

    protected int getReportInstanceStatus(String resContent) throws ReportExecutionException {

        if (resContent.length() == 0) {
            return ReportInstanceStatus.REPORT_INSTANCE_STATUS_OTHERS;
        }

        Matcher matcher = instanceStatusPattern.matcher(resContent);
        boolean cantFindStatus = false;
        String status = "";
        if (matcher.find()) {
            String instanceIdKeyValue = matcher.group();
            String [] split = instanceIdKeyValue.split(":");
            if (split.length != 2) {
                cantFindStatus = true;
            } else {
                status = split[1].replaceAll("\\s*", "");
            }
        }

        if (cantFindStatus) {
            throw new ReportExecutionException("Fail to get the report instance status.");
        }

        return Integer.parseInt(status);
    }

    private String getHttpRespondContent(HttpURLConnection conn) throws IOException {
        InputStream inputSteam = conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST ?
                conn.getInputStream() : conn.getErrorStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                (inputSteam)));

        StringBuilder output = new StringBuilder();
        String oneLine;
        while ((oneLine = br.readLine()) != null) {
            output.append(oneLine);
        }

        return output.toString();
    }

    private HttpURLConnection buildHttpConnection(URL url, String requestMethod) throws IOException {
        HttpURLConnection conn = conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod(requestMethod);
        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty("X-MSTR-ProjectID", this.projectId);
        conn.setRequestProperty("X-MSTR-AuthToken", this.authToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Cookie", this.cookie);

        return conn;
    }

    private boolean isStringEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private boolean validateParams() {
        return !isStringEmpty(this.authToken)
                && !isStringEmpty(this.cookie)
                && !isStringEmpty(this.libraryUrl)
                && !isStringEmpty(this.projectId)
                && !isStringEmpty(this.reportId);
    }

    public String getAuthToken() {
        return authToken;
    }

    public ReportExecutor setAuthToken(String authToken) {
        this.authToken = authToken;
        return this;
    }

    public String getCookie() {
        return cookie;
    }

    public ReportExecutor setCookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public String getProjectId() {
        return projectId;
    }

    public ReportExecutor setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getReportId() {
        return reportId;
    }

    public ReportExecutor setReportId(String reportId) {
        this.reportId = reportId;
        return this;
    }

    public String getLibraryUrl() {
        return libraryUrl;
    }

    public ReportExecutor setLibraryUrl(String libraryUrl) {
        this.libraryUrl = libraryUrl;
        return this;
    }

    public static void main(String[] args) throws ReportExecutionException, ReportExecutorInternalException {
        //String libraryPath = "http://10.23.32.173:8080/MicroStrategyLibrary";//"http://localhost:8081/web-dossier"; //http://10.197.34.76:8082/web-dossier1
        String libraryPath = "http://10.23.34.25:8080/MicroStrategyLibrary";

        String cookie = "JSESSIONID=CD801CEBEFE33DADD05BE841A675F694;";
        String authToken = "j8ptq2l9c5d8v5pnfkn2ibkblb";
        //String projectId = "B7CA92F04B9FAE8D941C3E9B7E0CD754"; //"B19DEDCC11D4E0EFC000EB9495D0F44F";
        String projectId = "B19DEDCC11D4E0EFC000EB9495D0F44F";
        // 256263D142248D56446F3A80AD100C06<-no prompt have prompt->E63835F111D5C49EC0000C881FDA1A4F
        String reportId = "13CFD83A458A68655A13CBA8D7C62CD5";//"028F2A1446B9ACA28C7ED79D75232B21";//"125F9FB34CEB75E36192E7A7C784EE52";

        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(String.join("/", libraryPath, "api", "auth/login"));
            post.setEntity(new ByteArrayEntity("{\"username\":\"administrator\"}".getBytes(StandardCharsets.UTF_8)));
            post.addHeader("Content-Type", "application/json");
            post.addHeader("accept", "application/json");
            CloseableHttpResponse response;
            response = client.execute(post);
            authToken = response.getHeaders("X-MSTR-AuthToken")[0].getElements()[0].getName();
            cookie = response.getHeaders("Set-Cookie")[0].getElements()[0].getName() + "="
                    + response.getHeaders("Set-Cookie")[0].getElements()[0].getValue();
        } catch (IOException e) {
            System.err.println("Use default auth token");
        }

        ReportExecutor reportExecutor = ReportExecutor.build()
                .setLibraryUrl(libraryPath).setCookie(cookie)
                .setAuthToken(authToken).setProjectId(projectId).setReportId(reportId);

        try {
            ResponseEntity<ReportInstance> res = reportExecutor.execute(ReportInstance.class);

            String res2 = reportExecutor.execute();

            System.out.println("res = \n" + res.getBody());

            System.out.println("res2 = \n" + res2);
        } catch (Exception e) {
            //e.printStackTrace();
        }

    }
}
