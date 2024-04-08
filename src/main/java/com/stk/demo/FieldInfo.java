package com.stk.demo;

import lombok.Data;

@Data
public class FieldInfo {
  private String fieldName;
  private String fieldType;
  private int hexLength;

  public FieldInfo(String fieldName, String fieldType, int hexLength) {
    this.fieldName = fieldName;
    this.fieldType = fieldType;
    this.hexLength = hexLength;
  }
}
