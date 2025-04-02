package com.aianalysis.requirement.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.aianalysis.requirement.model.RequirementVO;

@Mapper
public interface RequirementMapper {
    void insertRequirement(RequirementVO requirementVO); // 요청사항 입력
    List<RequirementVO> selectForUpdate(String reqNo); // 특정 요청사항 조회 (관리자가 수정을 위해)
    void updateRequirement(RequirementVO requirementVO); // 요청사항 수정
    void deleteRequirement(String reqNo); // 요청사항 삭제
    List<RequirementVO> selectAllRequirements(); // 요청사항 전체 조회 (관리자)
    List<RequirementVO> selectRequirements(String memId); // 요청사항 자기자신이 등록했을 경우의 조회
    List<RequirementVO> selectRequirement(String reqNo); // 요청사항 뷰로 조회
}
