package com.intelliservice.agentanalytics.model;

import java.util.Objects;


import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BasicResponseStatus
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-07-14T04:50:57.288Z[GMT]")


public class BasicResponseStatus   {
  @JsonProperty("responseType")
  private String responseType = null;

  @JsonProperty("statusCode")
  private String statusCode = null;

  public BasicResponseStatus responseType(String responseType) {
    this.responseType = responseType;
    return this;
  }

  /**
   * status response type
   * @return responseType
   **/
  @Schema(description = "status response type")
  
    public String getResponseType() {
    return responseType;
  }

  public void setResponseType(String responseType) {
    this.responseType = responseType;
  }

  public BasicResponseStatus statusCode(String statusCode) {
    this.statusCode = statusCode;
    return this;
  }

  /**
   * status code
   * @return statusCode
   **/
  @Schema(description = "status code")
  
    public String getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(String statusCode) {
    this.statusCode = statusCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicResponseStatus basicResponseStatus = (BasicResponseStatus) o;
    return Objects.equals(this.responseType, basicResponseStatus.responseType) &&
        Objects.equals(this.statusCode, basicResponseStatus.statusCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(responseType, statusCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicResponseStatus {\n");
    
    sb.append("    responseType: ").append(toIndentedString(responseType)).append("\n");
    sb.append("    statusCode: ").append(toIndentedString(statusCode)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
