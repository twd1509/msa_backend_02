package com.aianalysis.requirement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aianalysis.requirement.mapper.RequirementMapper;
import com.aianalysis.requirement.model.RequirementVO;

@Service
public class RequirementService {

    private final RequirementMapper rm;

    @Autowired
    public RequirementService(RequirementMapper rm) {
        this.rm = rm;
    }

    // 고객 요청사항 등록
    public void insertRequirement(RequirementVO requirementVO) {
        rm.insertRequirement(requirementVO);
    }
    // 고객 요청사항 수정
    public void updateRequirement(RequirementVO requirementVO) {
        rm.updateRequirement(requirementVO);
    }

    // 고객 요청사항 삭제
    public void requirementDel(String reqNo) {
        rm.deleteRequirement(reqNo);
    }
    
    // 요청사항 전체 조회
    public List<RequirementVO> selectAllRequirements() {
        return rm.selectAllRequirements();
    }

    // 특정 요청사항 조회 (수정용)
    public List<RequirementVO> selectRequirement(String reqNo) {
        return rm.selectForUpdate(reqNo);
    }

    // 회원이 본인이 등록한 요청 리스트 조회
    public List<RequirementVO> selectRequirements(String memId) {
        return rm.selectRequirements(memId);
    }
    
    public List<RequirementVO> viewRequirement(String reqNo){
    	return rm.selectRequirement(reqNo);
    }
}
