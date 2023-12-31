package b22.spartan.editor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import utilities.SpartanNewBase;
import io.restassured.http.ContentType;
import net.serenitybdd.junit5.SerenityTest;
import net.serenitybdd.rest.Ensure;
import net.serenitybdd.rest.SerenityRest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utilities.SpartanUtil;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.*;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static net.serenitybdd.rest.SerenityRest.given;

@Disabled
@SerenityTest
public class SpartanEditorPostTest extends SpartanNewBase {

    @DisplayName("Editor should be able to POST")
    @Test
    public void postSpartanAsEditor(){
        //create one spartan using util 
        Map<String,Object> bodyMap = SpartanUtil.getRandomSpartanMap();

        System.out.println("bodyMap = " + bodyMap);
        
        //send a post request as editor
        given()
                .auth().basic("editor","editor")
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(bodyMap)
                .log().body()
                .when()
                .post("/spartans")
                .then().log().all();

        Ensure.that("status code is 201",vRes -> vRes.statusCode(201));
        Ensure.that("content type is Json", vRes -> vRes.contentType(ContentType.JSON));
        Ensure.that("success message is A Spartan is Born",
                vRes -> vRes.body("success",is("A Spartan is Born!")));
        Ensure.that("id is not null", vRes -> vRes.body("data.id",notNullValue()));
        Ensure.that("name is correct",vRes -> vRes.body("data.name",is(bodyMap.get("name"))));
        Ensure.that("gender is correct",vRes -> vRes.body("data.gender",is(bodyMap.get("gender"))));
        Ensure.that("phone is correct",vRes -> vRes.body("data.phone",is(bodyMap.get("phone"))));

        String id = lastResponse().jsonPath().getString("data.id");

        Ensure.that("check locator header ends with/id",
                vR -> vR.header("Location",endsWith(id)));
    }

    @ParameterizedTest(name = "New Spartan {index} - name: {0}")
    @CsvFileSource(resources = "/SpartanData.csv",numLinesToSkip = 1)
    public void postSpartanWithCSV(String name, String gender,long phone){

        System.out.println("name = " + name);
        System.out.println("gender = " + gender);
        System.out.println("phone = " + phone);

        Map<String,Object> bodyMap = new LinkedHashMap<>();
        bodyMap.put("name",name);
        bodyMap.put("gender",gender);
        bodyMap.put("phone",phone);

        System.out.println("bodyMap = " + bodyMap);

        //send a post request as editor
        given()
                .auth().basic("editor","editor")
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(bodyMap)
                .log().body()
                .when()
                .post("/spartans")
                .then().log().all();

        //status code is 201
        Ensure.that("Status code is 201", x -> x.statusCode(201));
        //content type is Json
        Ensure.that("Content type is JSON", vR -> vR.contentType(ContentType.JSON));
        //success message is A Spartan is Born!
        Ensure.that("success message is correct",
                thenPart -> thenPart.body("success",is("A Spartan is Born!"))
        );
        //id is not null
        Ensure.that("id is not null",
                thenPart -> thenPart.body("data.id",notNullValue())
        );
        //name is correct
        Ensure.that("name is correct",
                thenPart -> thenPart.body("data.name",is(bodyMap.get("name")))
        );
        //gender is correct
        Ensure.that("gender is correct",
                thenPart -> thenPart.body("data.gender",is(bodyMap.get("gender")))
        );
        //phone is correct
        Ensure.that("phone is correct",
                thenPart -> thenPart.body("data.phone",is(bodyMap.get("phone")))
        );
        //check location header ends with newly generated id
        //get id and save
        String id = lastResponse().jsonPath().getString("data.id");

        Ensure.that("check location header ends with newly generated id",
                vR -> vR.header("Location",endsWith(id))
        );
    }


}
