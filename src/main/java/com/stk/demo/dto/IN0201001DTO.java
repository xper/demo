package com.stk.demo.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IN0201001DTO implements Serializable {
  long ullTkSerNum = 0x3812121281282828L;
  String szTkSerNumEx = "";
  int ucGameID = 0;

  @Override
  public String toString() {
    return String.format("IN0201001DTO(ullTkSerNum: %016d, szTkSerNumEx: %s, ucGameID: %02X)",
        ullTkSerNum,
        szTkSerNumEx,
        ucGameID);
  }
}
