package com.microstrategy.tools.integritymanager.executor;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.microstrategy.tools.integritymanager.exception.AnswerPromptException;
import com.microstrategy.tools.integritymanager.model.entity.mstr.prompt.*;
import com.microstrategy.tools.integritymanager.util.UrlHelper;
import com.microstrategy.webapi.EnumDSSXMLObjectTypes;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportPromptAnswerer {
    private static final String PATH_REPORT_INSTANCE = "reports/{{reportId}}/instances/{{instanceId}}";
    private static final String PATH_GET_REPORT_INSTANCE_PROMPT = "prompts";
    /*
    [
    {
        "id": "E638361911D5C49EC0000C881FDA1A4F",
        "key": "E638361911D5C49EC0000C881FDA1A4F@0@10",
        "type": "ELEMENTS",
        "name": "Category selection",
        "title": "Category selection",
        "required": true,
        "closed": false,
        "source": {
            "name": "Category",
            "id": "8D679D3711D3E4981000E787EC6DE8A4",
            "type": 12
        },
        "defaultAnswer": [
            {
                "id": "h2;8D679D3711D3E4981000E787EC6DE8A4",
                "name": "Electronics"
            }
        ],
        "answers": [
            {
                "id": "h2;8D679D3711D3E4981000E787EC6DE8A4",
                "name": "Electronics"
            }
        ]
    }
]
     */
    private static final String PATH_GET_REPORT_INSTANCE_ELEMENT_PROMPT_INFO = "prompts/{{promptId}}/elements";
    /*
    {
    "elements": [
        {
            "id": "h1;8D679D3711D3E4981000E787EC6DE8A4",
            "name": "Books"
        },
        {
            "id": "h2;8D679D3711D3E4981000E787EC6DE8A4",
            "name": "Electronics"
        },
        {
            "id": "h3;8D679D3711D3E4981000E787EC6DE8A4",
            "name": "Movies"
        },
        {
            "id": "h4;8D679D3711D3E4981000E787EC6DE8A4",
            "name": "Music"
        }
    ]
}
     */
    private static final String PATH_GET_REPORT_INSTANCE_OBJECT_PROMPT_INFO = "prompts/{{promptId}}/object";
    private static final String PATH_PUT_ANSWER_PROMPT = "prompts/answers";
    /*
    {
  "prompts": [
    {
      "key": "{{promptKey}}",
      "type": "{{promptType}}",
      "answers": [
		{
            "id": "h1;8D679D3711D3E4981000E787EC6DE8A4",
            "name": "Books"
        }
      ]
    }
  ]
}
     */
    private static final String PATH_GET_REPOST_STATUS = "status";

    private final String libraryUrl;

    //private final String reportId = "";
    private final String objectId;
    private final int objectType;
    private String urlPrefix;
    private String instanceId;

    private CloseableHttpClient httpClient;

    public ReportPromptAnswerer(ReportExecutor reportExecutor) {
        this.objectId = reportExecutor.getReportId();
        this.libraryUrl = reportExecutor.getLibraryUrl();
        this.objectType = EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition;

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("X-MSTR-ProjectID", reportExecutor.getProjectId()));
        headers.add(new BasicHeader("X-MSTR-AuthToken", reportExecutor.getAuthToken()));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Cookie", reportExecutor.getCookie()));
        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .build();
    }

    public ReportPromptAnswerer(RestParams restParams, String objectId, int objectType) {
        this.objectId = objectId;
        this.libraryUrl = restParams.getLibraryUrl();
        this.objectType = objectType;

        List<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("Accept", "*/*"));
        headers.add(new BasicHeader("X-MSTR-ProjectID", restParams.getProjectId()));
        headers.add(new BasicHeader("X-MSTR-AuthToken", restParams.getAuthToken()));
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Cookie", restParams.getCookies().get(0)));
        httpClient = HttpClientBuilder.create()
                .setDefaultHeaders(headers)
                .build();
    }

    public void answer(String instanceId) throws AnswerPromptException {
        if (objectType == EnumDSSXMLObjectTypes.DssXmlTypeReportDefinition) {
            answerReportPrompts(instanceId);
            return;
        }
        else if (objectType == EnumDSSXMLObjectTypes.DssXmlTypeDocumentDefinition) {
            answerDossierPrompts(instanceId);
            return;
        }
        assert false;
    }

    private void answerReportPrompts(String reportInstanceId) throws AnswerPromptException {
        this.instanceId = reportInstanceId;
        this.urlPrefix = UrlHelper.replaceUrl(
                UrlHelper.replaceUrl(PATH_REPORT_INSTANCE, UrlHelper.PathNames.REPORT_ID, this.objectId),
                UrlHelper.PathNames.REPORT_INSTANCE_ID, this.instanceId);
        List<Prompt> prompts = getReportInstancePrompts();
        if (prompts == null) {
            throw new AnswerPromptException("Get null from report instance");
        }
        Map<String, List<PromptAnswer>> answers = preparePromptAnswer(prompts);
        answerPrompts(prompts, answers);
    }

    private void answerDossierPrompts(String instanceId) throws AnswerPromptException {
        this.instanceId = instanceId;
        this.urlPrefix = String.format("documents/%s/instances/%s", objectId, instanceId);
        List<Prompt> prompts = getReportInstancePrompts();
        if (prompts == null) {
            throw new AnswerPromptException("Get null from document/dossier instance");
        }
        Map<String, List<PromptAnswer>> answers = preparePromptAnswer(prompts);
        answerPrompts(prompts, answers);
    }

    private void answerPrompts(List<Prompt> prompts, Map<String, List<PromptAnswer>> answersMap) throws AnswerPromptException {
        RestPromptAnswer restPromptAnswer = new RestPromptAnswer(new ArrayList<>());
        for (Prompt prompt : prompts) {
            restPromptAnswer.getPrompts().add(constructPromptData(prompt, answersMap.getOrDefault(prompt.getId(), null)));
        }
        sendPromptData(restPromptAnswer);
    }

    private AnswerPromptData constructPromptData(Prompt prompt, List<PromptAnswer> answers) {
        AnswerPromptData data = new AnswerPromptData();
        data.setKey(prompt.getKey());
        data.setId(prompt.getId());
        data.setType(prompt.getType());
        data.setAnswers(answers);
        return data;
    }

    private void sendPromptData(RestPromptAnswer data) throws AnswerPromptException {
        try {
            String url = UrlHelper.joinUrl(this.libraryUrl, "api", this.urlPrefix, PATH_PUT_ANSWER_PROMPT);
            HttpPut httpPut = new HttpPut(url);
            Gson gson = new Gson();
            httpPut.setEntity(new StringEntity((gson.toJson(data))));
            CloseableHttpResponse response = httpClient.execute(httpPut);
            if (response.getStatusLine().getStatusCode() != 204) {
                throw new AnswerPromptException("Failed to send prompt data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<PromptAnswer>> preparePromptAnswer(List<Prompt> prompts) throws AnswerPromptException {
        Map<String, List<PromptAnswer>> answersMap = new HashMap<>();
        for (Prompt prompt : prompts) {
            answersMap.put(prompt.getId(), getAnswersForPrompt(prompt, getPromptInfo(prompt)));
        }
        return answersMap;
    }

    private List<PromptAnswer> getAnswersForPrompt(Prompt prompt, PromptInfo promptInfo) throws AnswerPromptException {
        List<PromptAnswer> answers = getUserAnswersForPrompt(prompt, promptInfo);
        if (answers.size() < 1) {
            answers = getDefaultAnswersFromPrompt(prompt, promptInfo);
        }
        return answers;
    }

    private List<PromptAnswer> getDefaultAnswersFromPrompt(Prompt prompt, PromptInfo promptInfo) throws AnswerPromptException {
        List<PromptAnswer> answers = new ArrayList<>();
        switch (prompt.getType().toUpperCase()) {
            case "ELEMENTS": {
                answers.add(((ElementPromptInfo) promptInfo).getElements().get(0));
                break;
            }
            default: {
                throw new AnswerPromptException("Cannot answer prompt by default");
            }
        }
        return answers;
    }

    private List<PromptAnswer> getUserAnswersForPrompt(Prompt prompt, PromptInfo promptInfo) {
        List<PromptAnswer> answers = new ArrayList<>();

        return answers;
    }

    private PromptInfo getPromptInfo(Prompt prompt) throws AnswerPromptException {
        PromptInfo promptInfo = null;
        switch (prompt.getType().toUpperCase()) {
            case "ELEMENTS": {
                promptInfo = queryElementPromptInfo(prompt);
                break;
            }
            default: {
                throw new AnswerPromptException("Unsupported prompt type.");
            }
        }
        return promptInfo;
    }

    private ElementPromptInfo queryElementPromptInfo(Prompt prompt) throws AnswerPromptException {
        try {
            String url = UrlHelper.joinUrl(this.libraryUrl, "api", this.urlPrefix, UrlHelper.replaceUrl(PATH_GET_REPORT_INSTANCE_ELEMENT_PROMPT_INFO, UrlHelper.PathNames.PROMPT_ID, prompt.getId()));
            HttpGet getPromptInfo = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(getPromptInfo);
            Gson gson = new Gson();
            return gson.fromJson(EntityUtils.toString(response.getEntity()), ElementPromptInfo.class);
        } catch (IOException e) {
            throw new AnswerPromptException("Failed to get element prompt info", e);
        }
    }

    private List<Prompt> getReportInstancePrompts() throws AnswerPromptException {
        try {
            String url = UrlHelper.joinUrl(this.libraryUrl, "api", this.urlPrefix, PATH_GET_REPORT_INSTANCE_PROMPT);
            HttpGet getPromptReq = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(getPromptReq);

            String promptInfoString = EntityUtils.toString(response.getEntity());
            Gson gson = new Gson();
            return gson.fromJson(promptInfoString, new TypeToken<List<Prompt>>() {
            }.getType());
        } catch (IOException e) {
            throw new AnswerPromptException("Get prompt for report instance failed in Rest request", e);
        } catch (JsonSyntaxException je) {
            System.err.println("Get prompt for report instance failed with error");
            je.printStackTrace();
        }

        return null;
    }
}
