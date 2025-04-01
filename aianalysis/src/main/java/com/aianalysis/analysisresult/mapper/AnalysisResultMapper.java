package com.aianalysis.analysisresult.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import com.aianalysis.analysisresult.model.AnalysisResultVO;

@Mapper
public interface AnalysisResultMapper {
	
	// 등록 전 email 정보 가져오기
	public String selectEmailByReqNo(int ReqNo);
	// create
	public void insertAnlsRslt(AnalysisResultVO analysisResultVO);
	
	// read (사용자 마이페이지)
	public AnalysisResultVO selectAnlsRsltByEmail(String email);
	// read (상세 페이지)
	public AnalysisResultVO selectAnlsRsltByReqNo(int reqNo);
	
	
	// 메일 보내기 전 상태 확인
	public boolean selectEmailTransmissionStatus(int anlsRsltNo);
	// 메일 보낸 정보 등록
	public void updateEmailTransmissionStatus(int anlsRsltNo, LocalDateTime currentTime);
	
	// update
	public void updateAnlsRslt(AnalysisResultVO analysisResultVO);

	// delete
	public int deleteAnlsRsltByAnlsReqNo(int reqNo);
}
