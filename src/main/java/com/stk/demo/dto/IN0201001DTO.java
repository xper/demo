package com.stk.demo.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IN0201001DTO extends HeaderDTO {
  long ullTkSerNum = 0x3812121281282828L;
  String szTkSerNumEx = "";
  int ucGameID = 0;

  @Override
  public String toString() {
    return String.format(
        " * HeaderDTO(lLMagicNumber: %d, ucMessageID: %02X, ucServiceID: %02X, usVersion: %04X)\n * IN0201001DTO(ullTkSerNum: %016d, szTkSerNumEx: %s, ucGameID: %02X)",
        lLMagicNumber,
        (int) ucMessageID,
        (int) ucServiceID,
        (int) usVersion,
        ullTkSerNum,
        szTkSerNumEx,
        ucGameID);
  }
}
