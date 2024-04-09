package com.stk.demo.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class HeaderDTO implements Serializable {
  long lLMagicNumber = 0x3812121281282828L; // 0x3812121281282828L: 4040311686390360104 Betman, vtx, 발매, 내부 공통 전문 시작
                                            // 0x3812121281282000L: 4040311686390358016 3Way Ack 응답 전문 시작
  int ucCrypType = (int) 0x00;
  int ucTermType = (int) 0xf0;
  int ucMessageID;
  int ucServiceID;
  short usVersion;

  @Override
  public String toString() {
    return String.format("HeaderDTO(lLMagicNumber: %d, ucCrypType: %02X, ucTermType: %02X, ucMessageID: %02X, ucServiceID: %02X, usVersion: %04X)",
        lLMagicNumber,
        (int) ucCrypType,
        (int) ucTermType,
        (int) ucMessageID,
        (int) ucServiceID,
        (int) usVersion);
  }
}
