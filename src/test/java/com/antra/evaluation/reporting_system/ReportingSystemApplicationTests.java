package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.api.request.ExcelRequest;

import com.antra.evaluation.reporting_system.pojo.api.request.MultiExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.response.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import javax.validation.Validator;
import javax.validation.constraints.AssertTrue;
import java.io.File;

import java.util.*;


import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportingSystemApplicationTests {

    @Value("http://localhost:${local.server.port}")
    private String APPLICATION_URI;

    private static Validator validator;

    private final ExcelRequest singleSheetExcelRequest = new ExcelRequest();

    private final ExcelRequest multiSheetExcelRequest = new ExcelRequest();

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        for (int i = 0; i <= 10; i++) {
            new File(i + ".xlsx").delete();
        }
    }

    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpData() {
        singleSheetExcelRequest.setHeaders(Arrays.asList("Student #", "Name", "Class", "Score"));
        singleSheetExcelRequest.setDescription("Student Math Course Report");
        List<List<String>> SingleSheetExcelData = new ArrayList<>();
        SingleSheetExcelData.add(Arrays.asList("s-001", "James", "Class-A", "A+"));
        SingleSheetExcelData.add(Arrays.asList("s-002", "Robert", "Class-A", "A"));
        SingleSheetExcelData.add(Arrays.asList("s-003", "Jennifer", "Class-A", "A"));
        SingleSheetExcelData.add(Arrays.asList("s-004", "Linda", "Class-B", "B"));
        SingleSheetExcelData.add(Arrays.asList("s-005", "Elizabeth", "Class-B", "B+"));
        SingleSheetExcelData.add(Arrays.asList("s-006", "Susan", "Class-C", "A"));
        SingleSheetExcelData.add(Arrays.asList("s-007", "Jessica", "Class-C", "A+"));
        SingleSheetExcelData.add(Arrays.asList("s-008", "Sarah", "Class-A", "B"));
        SingleSheetExcelData.add(Arrays.asList("s-009", "Thomas", "Class-A", "B-"));
        SingleSheetExcelData.add(Arrays.asList("s-010", "Joseph", "Class-B", "A-"));
        SingleSheetExcelData.add(Arrays.asList("s-011", "Charles", "Class-C", "A"));
        SingleSheetExcelData.add(Arrays.asList("s-012", "Lisa", "Class-D", "B"));
        singleSheetExcelRequest.setData(SingleSheetExcelData);

        multiSheetExcelRequest.setHeaders(Arrays.asList("Student #", "Name", "Class", "Score"));
        multiSheetExcelRequest.setDescription("Student Math Course Report");
        List<List<String>> multiSheetExcelData = new ArrayList<>();
        multiSheetExcelData.add(Arrays.asList("s-001", "James", "Class-A", "A+"));
        multiSheetExcelData.add(Arrays.asList("s-002", "Robert", "Class-A", "A"));
        multiSheetExcelData.add(Arrays.asList("s-003", "Jennifer", "Class-A", "A"));
        multiSheetExcelData.add(Arrays.asList("s-004", "Linda", "Class-B", "B"));
        multiSheetExcelData.add(Arrays.asList("s-005", "Elizabeth", "Class-B", "B+"));
        multiSheetExcelData.add(Arrays.asList("s-006", "Susan", "Class-C", "A"));
        multiSheetExcelData.add(Arrays.asList("s-007", "Jessica", "Class-C", "A+"));
        multiSheetExcelData.add(Arrays.asList("s-008", "Sarah", "Class-A", "B"));
        multiSheetExcelData.add(Arrays.asList("s-009", "Thomas", "Class-A", "B-"));
        multiSheetExcelData.add(Arrays.asList("s-010", "Joseph", "Class-B", "A-"));
        multiSheetExcelData.add(Arrays.asList("s-011", "Charles", "Class-C", "A"));
        multiSheetExcelData.add(Arrays.asList("s-012", "Lisa", "Class-D", "B"));
        multiSheetExcelRequest.setData(multiSheetExcelData);
        multiSheetExcelRequest.setSplitBy("Score");

    }

    @Test
    public void testExcelGeneration() {
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse excelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        assertTrue(excelResponse.getMessage().equals("Generated Successfully"));
        LinkedHashMap<String, Object> body = (LinkedHashMap) excelResponse.getBody();
        String id = (String) body.get("id");
        String fileSize = (String) body.get("fileSize");
        assertTrue(id.equals("Excel-1"));
        assertTrue(!fileSize.equals("OB"));

    }

    @Test
    public void testMultiSheetExcelGeneration() {
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse excelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel/auto", multiSheetExcelRequest, ExcelResponse.class);
        assertTrue(excelResponse.getMessage().equals("Generated Successfully"));
        LinkedHashMap<String, Object> body = (LinkedHashMap) excelResponse.getBody();
        String id = (String) body.get("id");
        String fileSize = (String) body.get("fileSize");
        assertTrue(id.equals("Excel-1"));
        assertTrue(!fileSize.equals("OB"));
    }

    @Test
    public void testMultiExcelFilesGenerate() {
        MultiExcelRequest model = new MultiExcelRequest();
        List<ExcelRequest> excelRequestList = new ArrayList<>();
        excelRequestList.add(singleSheetExcelRequest);
        excelRequestList.add(multiSheetExcelRequest);
        model.setExcels(excelRequestList);
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse excelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel/multi", model, ExcelResponse.class);
        List<LinkedHashMap<String, Object>> body = (List) excelResponse.getBody();
        for (int i = 1; i <= body.size(); i++) {
            LinkedHashMap<String, Object> excelBody = body.get(i - 1);
            LinkedHashMap<String, Object> excelBodyData = (LinkedHashMap) excelBody.get("body");
            String id = (String) excelBodyData.get("id");
            String fileSize = (String) excelBodyData.get("fileSize");
            assertTrue(id.equals("Excel-" + i));
            assertTrue(!fileSize.equals("OB"));
        }
    }

    @Test
    public void testListExcels() {

        RestTemplate restTemplate = new RestTemplate();
        Set<String> idSet = new HashSet<>();
        ExcelResponse singleSheetExcelResponse1 = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        ExcelResponse singleSheetExcelResponse2 = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        ExcelResponse multiSheetExcelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel/auto", multiSheetExcelRequest, ExcelResponse.class);
        List<ExcelResponse> excelResponseList = new ArrayList<>();
        excelResponseList.add(singleSheetExcelResponse1);
        excelResponseList.add(singleSheetExcelResponse2);
        excelResponseList.add(multiSheetExcelResponse);
        ExcelResponse listExcelResponse = restTemplate.getForObject(APPLICATION_URI + "/excel", ExcelResponse.class);
        List<LinkedHashMap<String, Object>> mainBody = (List) listExcelResponse.getBody();
        for (int i = 0; i < mainBody.size(); i++) {
            LinkedHashMap<String, Object> excelBody = mainBody.get(i);
            String id = (String) excelBody.get("id");
            String fileSize = (String) excelBody.get("fileSize");
            idSet.add(id);
            assertTrue(!fileSize.equals("OB"));
        }
        assertTrue(idSet.size() == excelResponseList.size());
        for (ExcelResponse ex : excelResponseList) {
            LinkedHashMap<String, Object> body = (LinkedHashMap) ex.getBody();
            String id = (String) body.get("id");
            assertTrue(idSet.contains(id));

        }

    }

    @Test
    public void testDownloadExcel() {
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse excelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        LinkedHashMap<String, Object> body = (LinkedHashMap) excelResponse.getBody();
        String id = (String) body.get("id");
        when().get(APPLICATION_URI + "/excel/" + id + "/content").peek().then().assertThat().statusCode(200);
    }


    @Test
    public void testDownloadMultiExcels() {
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse singleSheetExcelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        LinkedHashMap<String, Object> body1 = (LinkedHashMap) singleSheetExcelResponse.getBody();
        String id1 = (String) body1.get("id");
        ExcelResponse multiSheetExcelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel/auto", multiSheetExcelRequest, ExcelResponse.class);
        LinkedHashMap<String, Object> body2 = (LinkedHashMap) multiSheetExcelResponse.getBody();
        String id2 = (String) body2.get("id");
        when().get(APPLICATION_URI + "/excel/multi/content?fileId=" + id1 + "&fileId=" + id2).peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testDeleteExcel() {
        RestTemplate restTemplate = new RestTemplate();
        ExcelResponse excelResponse = restTemplate.postForObject(APPLICATION_URI + "/excel", singleSheetExcelRequest, ExcelResponse.class);
        LinkedHashMap<String, Object> body = (LinkedHashMap) excelResponse.getBody();
        String id = (String) body.get("id");
        when().delete(APPLICATION_URI + "/excel/" +id).peek().then().assertThat().statusCode(200);
    }

}
