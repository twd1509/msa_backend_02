package com.aianalysis.analysisresult.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AnalysisResultVO {
    // 멤버변수
    private int anlsRsltNo;           // 분석 결과 번호
    private int reqNo;                // 요청 번호
    private String email;             // 사용자 이메일
    private String anlsRslt;          // 분석 결과
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH시 mm분 ss초")
    private LocalDateTime anlsRsltYmd; // 분석 결과 날짜
    private boolean emlTrsmYn;        // 결과 메일 전송 여부
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 HH시 mm분 ss초")
    private LocalDateTime emlTrsmYmd; // 메일 전송 날짜

    // 게터, 세터
    public int getAnlsRsltNo() { return anlsRsltNo; }
    public void setAnlsRsltNo(int anlsRsltNo) { this.anlsRsltNo = anlsRsltNo; }

    public int getReqNo() { return reqNo; }
    public void setReqNo(int reqNo) { this.reqNo = reqNo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAnlsRslt() { return anlsRslt; }
    public void setAnlsRslt(String anlsRslt) { this.anlsRslt = anlsRslt; }

    public LocalDateTime getAnlsRsltYmd() { return anlsRsltYmd; }
    public void setAnlsRsltYmd(LocalDateTime anlsRsltYmd) { this.anlsRsltYmd = anlsRsltYmd; }

    public boolean isEmlTrsmYn() { return emlTrsmYn; }
    public void setEmlTrsmYn(boolean emlTrsmYn) { this.emlTrsmYn = emlTrsmYn; }

    public LocalDateTime getEmlTrsmYmd() { return emlTrsmYmd; }
    public void setEmlTrsmYmd(LocalDateTime emlTrsmYmd) { this.emlTrsmYmd = emlTrsmYmd; }
}
