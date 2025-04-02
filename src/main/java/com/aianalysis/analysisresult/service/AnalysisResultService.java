package com.aianalysis.analysisresult.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.aianalysis.analysisresult.mapper.AnalysisResultMapper;
import com.aianalysis.analysisresult.model.AnalysisResultVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class AnalysisResultService {
	
	@Autowired
	AnalysisResultMapper analysisResultMapper;
	
    @Autowired
    private JavaMailSender emailSender;

    //이메일 전송 전 상태 확인
    public boolean checkEmailTransmissionStatus(int anlsRsltNo) {
    	return analysisResultMapper.selectEmailTransmissionStatus(anlsRsltNo);
    }
    
 // 이메일 전송
    public boolean sendEmail(AnalysisResultVO anlsRslt) {
        try {
            // 메시지 텍스트 부분 포맷 정리하기
            StringBuilder textBuilder = new StringBuilder();
            textBuilder.append("<h2>요청 번호: ").append(anlsRslt.getReqNo()).append("</h2>");
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");
            textBuilder.append("<h3>분석 완료 날짜: ").append(anlsRslt.getAnlsRsltYmd().format(formatter)).append("</h3>");
            textBuilder.append("<h3>자세한 결과:</h3>");
            
            textBuilder.append("<table style='border-collapse: collapse; width: 50%;'>");
            textBuilder.append("<tr><th style='border: 1px solid black; padding: 8px;'>검출된 항목</th><th style='border: 1px solid black; padding: 8px;'>신뢰도(%)</th></tr>");
            String[] results = anlsRslt.getAnlsRslt().split("\\|");
            for (String result : results) {
                String[] parts = result.split(":"); // "항목: 신뢰도" 형식으로 나누기
                if (parts.length == 2) {
                    textBuilder.append("<tr>");
                    textBuilder.append("<td style='border: 1px solid black; padding: 8px;'>").append(parts[0].trim()).append("</td>");
                    textBuilder.append("<td style='border: 1px solid black; padding: 8px;'>").append(parts[1].trim()).append("</td>");
                    textBuilder.append("</tr>");
                }
            }
            textBuilder.append("</table>");

            // 메시지 준비
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(anlsRslt.getEmail());
            helper.setSubject("분석요청번호 " + anlsRslt.getReqNo() + "번에 대한 분석 결과입니다.");
            helper.setText(textBuilder.toString(), true); // true: HTML 콘텐츠로 설정

            // 메시지 전송
            emailSender.send(message);

            // 메시지 전송 결과 반영하기
            
            analysisResultMapper.updateEmailTransmissionStatus(anlsRslt.getAnlsRsltNo(), LocalDateTime.now());
            return true; // 이메일 전송 성공
        } catch (MailException | MessagingException e) {
            System.err.println("이메일 전송 실패: " + e.getMessage());
            return false; // 이메일 전송 실패
        }
    }
    
    //분석 결과 등록 전 이메일 받기
    public String readEmailByReqNo(String reqNo) {
    	return analysisResultMapper.selectEmailByReqNo(Integer.parseInt(reqNo));
    }
    
	public void insertAnalysisResult(AnalysisResultVO analysisResultVO) {
		analysisResultVO.setAnlsRsltYmd(LocalDateTime.now());
		analysisResultMapper.insertAnlsRslt(analysisResultVO);
	}
	
	public AnalysisResultVO readAnalysisResultByReqNo(int reqNo) {
		return analysisResultMapper.selectAnlsRsltByReqNo(reqNo);
	}
	
	
	public int deleteData(int reqNo) {
		return analysisResultMapper.deleteAnlsRsltByAnlsReqNo(reqNo);
	}
}
