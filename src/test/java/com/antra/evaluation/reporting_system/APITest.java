package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;


public class APITest {
    @Mock
    ExcelService excelService;

    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
    }

    @Test
    public void testGetExcelMetaData() {
        ExcelFile testExcelFile = ExcelFile.builder().id("Excel-1").build();
        Mockito.when(excelService.getExcelDataById(anyString())).thenReturn(testExcelFile);
        given().accept("application/json").get("/excel/abc123/data").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testExcelGeneration() throws IOException {
        ExcelFile testExcelFile = ExcelFile.builder().id("Excel-1").build();
        Mockito.when(excelService.saveExcel(any())).thenReturn(testExcelFile);
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}")
                .post("/excel").peek().
                then().assertThat()
                .statusCode(201)
                .body("body.id", Matchers.equalTo(testExcelFile.getId())).body("message", Matchers.equalTo("Generated Successfully"));
    }

    @Test
    void testExcelGenerationButNoHeaders() throws IOException {
        ExcelFile testExcelFile = ExcelFile.builder().id("Excel-1").build();
        Mockito.when(excelService.saveExcel(any())).thenReturn(testExcelFile);
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}")
                .post("/excel/auto").peek().
                then().assertThat()
                .statusCode(400)
                .body("message", Matchers.equalTo("Excel Data Error: Input data format has error, headers and data must exist and in same length. " +
                        "For multi-sheet request, the field for split by cannot be null"));
    }



    @Test
    public void testMultiSheetExcelGeneration() throws IOException {
        ExcelFile testExcel = ExcelFile.builder().id("Excel-1").build();
        Mockito.when(excelService.saveExcel(any())).thenReturn(testExcel);
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]], \"splitBy\":\"Age\"}")
                .post("/excel/auto").peek().
                then().assertThat()
                .statusCode(201)
                .body("body.id", Matchers.equalTo(testExcel.getId())).body("message", Matchers.equalTo("Generated Successfully"));
    }


    @Test
    public void testMultiExcelFilesGeneration() throws IOException {
        ExcelFile testExcelFile1 = ExcelFile.builder().id("Excel-1").build();
        ExcelFile testExcelFile2 = ExcelFile.builder().id("Excel-2").build();
        Mockito.when(excelService.saveExcel(any())).thenReturn(testExcelFile1);
        Mockito.when(excelService.saveExcel(any())).thenReturn(testExcelFile2);
        given().accept("application/json").contentType(ContentType.JSON)
                .body("{\"excels\":[{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}," +
                        "{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]], \"splitBy\":\"Name\"}]}")
                .post("/excel/multi").peek().
                then().assertThat()
                .statusCode(201)
                .body("message", Matchers.equalTo("Generated finished"));
    }

    @Test
    public void testFileDownload() throws FileNotFoundException {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        given().accept("application/json").get("/excel/temp/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testFileDownloadNotExist() {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(null);
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(404).body("errorCode", Matchers.equalTo(1))
                .body("message", Matchers.equalTo("Excel FIle Error: This file doesnt`t exist"));
    }

    @Test
    public void testMultiFileDownload() {
        Mockito.when(excelService.getExcelDataById(anyString())).thenReturn(new ExcelFile());
        given().accept("application/json").get("/excel/multi/content?fileId=Excel-1&fileId=Excel-2").peek().
                then().assertThat()
                .statusCode(200);
    }


    @Test
    public void testListFiles() {
        List<ExcelFile> excelFiles = new ArrayList<>();
        ExcelFile testExcelFile1 = ExcelFile.builder().id("Excel-1").build();
        ExcelFile testExcelFile2 = ExcelFile.builder().id("Excel-2").build();
        excelFiles.add(testExcelFile1);
        excelFiles.add(testExcelFile2);
        Mockito.when(excelService.getAllFiles()).thenReturn(excelFiles);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200).body("message", Matchers.equalTo("All Excel Files"))
                .body("body[0].id", Matchers.equalTo("Excel-1"))
                .body("body[1].id", Matchers.equalTo("Excel-2"));
    }

    @Test
    public void testFileDeletion() {
        ExcelFile testExcelFile = ExcelFile.builder().id("Excel-1").build();
        Mockito.when(excelService.getExcelDataById(anyString())).thenReturn(testExcelFile);
        given().accept("application/json").delete("/excel/Excel-1").peek().
                then().assertThat()
                .statusCode(200).body("message", Matchers.equalTo("Deleted Successfully"))
                .body("body.id", Matchers.equalTo("Excel-1"));

    }


}
