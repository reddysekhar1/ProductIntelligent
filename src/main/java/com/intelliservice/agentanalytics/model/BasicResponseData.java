package com.intelliservice.agentanalytics.model;

import java.util.Objects;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * BasicResponse
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-07-14T04:50:57.288Z[GMT]")


public class BasicResponseData<T>   {
  @JsonProperty("status")
  private BasicResponseStatus status = null;

  @JsonProperty("data")
  private T data = null;

  @JsonProperty("errors")
  private String errors = null;

  public BasicResponseData status(BasicResponseStatus status) {
    this.status = status;
    return this;
  }

  public BasicResponseData() {}
  public BasicResponseData(String responseType, String statusCode, T data, String error) {
	  this.status = new BasicResponseStatus()
			  .responseType(responseType)
			  .statusCode(statusCode);
	  this.data = data;
	  this.errors = error;
  }
  /**
   * Get status
   * @return status
   **/
  @Schema(description = "")
  
    @Valid
    public BasicResponseStatus getStatus() {
    return status;
  }

  public BasicResponseData setStatus(BasicResponseStatus status) {
    this.status = status;
    return this;
  }





  public T getData() {
	return data;
}

public void setData(T data) {
	this.data = data;
}

public BasicResponseData errors(String errors) {
    this.errors = errors;
    return this;
}

  /**
   * Get errors
   * @return errors
   **/
  @Schema(description = "")
  
    public String getErrors() {
    return errors;
  }

  public void setErrors(String errors) {
    this.errors = errors;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicResponseData basicResponse = (BasicResponseData) o;
    return Objects.equals(this.status, basicResponse.status) &&
        Objects.equals(this.data, basicResponse.data) &&
        Objects.equals(this.errors, basicResponse.errors);
  }

  @Override
  public int hashCode() {
    return Objects.hash(status, data, errors);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicResponse {\n");
    
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    errors: ").append(toIndentedString(errors)).append("\n");
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
