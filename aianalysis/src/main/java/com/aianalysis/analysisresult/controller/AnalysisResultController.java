package com.aianalysis.analysisresult.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aianalysis.analysisresult.model.AnalysisResultVO;
import com.aianalysis.analysisresult.service.AnalysisResultService;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class AnalysisResultController {

	@Autowired
	private AnalysisResultService analysisResultService;
	
	@RequestMapping("/api/anlsRslt/isAnalyzed")
	public boolean isAnalyzed(@RequestBody int reqNo) {
		if(analysisResultService.readAnalysisResultByReqNo(reqNo) != null) {
			return true;
		}else {
			return false;
		}
	}
	
	@RequestMapping("/api/anlsRslt/detail/read")
	public AnalysisResultVO readAnlsRslt(@RequestBody int reqNo) {
		return analysisResultService.readAnalysisResultByReqNo(reqNo);
	}
	

	
	@RequestMapping("/api/email/send")
	public int sendEmail(@RequestBody int reqNo) {
		AnalysisResultVO anlsRslt = analysisResultService.readAnalysisResultByReqNo(reqNo);
		if(anlsRslt != null) {
			if(!analysisResultService.checkEmailTransmissionStatus(anlsRslt.getAnlsRsltNo())) {
				if(analysisResultService.sendEmail(anlsRslt)) {
					// 정상적으로 이메일 발송됨
					return 2;
				}else{
					// 이메일 발송중 에러 발생
					return 1;
				}
			}else{
				// 이메일 보낸 이력 존재
				return 0;
			}
		}else {
			//분석결과가 생성되지 않음.
			return 3;
		}
	}
}
