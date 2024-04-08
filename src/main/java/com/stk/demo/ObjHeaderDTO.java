package com.stk.demo;

import java.io.Serializable;

public class ObjHeaderDTO implements Serializable {
  long lLMagicNumber = 0x3812121281282828L; // 0x3812121281282828L: Betman, vtx, 발매, 내부 공통 전문 시작
                                            // 0x3812121281282000L: 3Way Ack 응답 전문 시작
  char ucCrypType = (char) 0x00;
  char ucTermType = (char) 0xf0;
  char ucMessageID;
  char ucServiceID;
  short usVersion = 0x01;

  ObjHeaderDTO(char ucMessageID, char ucServiceID) {
    this.ucMessageID = ucMessageID;
    this.ucServiceID = ucServiceID;
  }

  @Override
  public String toString() {
    return String.format("HeaderDTO(lLMagicNumber: %d, ucMessageID: %c, ucServiceID: %c)", lLMagicNumber, ucMessageID,
    ucServiceID);
  }
}
