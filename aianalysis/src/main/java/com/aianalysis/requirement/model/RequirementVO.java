package com.aianalysis.requirement.model;

import java.time.LocalDateTime;

public class RequirementVO {
    private String reqNo;					// 요청 번호 (PK)
    private String title;					// 글 제목
    private String content;					// 요청 내용
    private String memId;					// 글 작성자 ID
    private String status;					// 영상 분석 상태
    private String fileTitle;				// 업로드 된 이미지 제목
    private LocalDateTime regDt;    		// 요청 등록일
    private LocalDateTime updateDate;		// 요청 등록일
    

    // Getter & Setter
    public String getReqNo() { 
    	return reqNo; 
    }
    public void setReqNo(String reqNo) { 
    	this.reqNo = reqNo; 
    }

    public String getContent() { 
    	return content; 
    }
    public void setContent(String content) { 
    	this.content = content; 
    }
    
    public String getTitle() { 
    	return title; 
    }
    public void setTitle(String title) { 
    	this.title = title; 
    }

    public String getMemId() { 
    	return memId; 
    }
    public void setMemId(String memId) { 
    	this.memId = memId; 
    }

    public String getStatus() { 
    	return status; 
    }
    public void setStatus(String status) { 
    	this.status = status; 
    }

    public String getFileTitle() { 
    	return fileTitle; 
    }
    public void setFileTitle(String fileTitle) { 
    	this.fileTitle = fileTitle; 
    }

    public LocalDateTime getRegDt() { 
    	return regDt; 
    }
    public void setReqDate(LocalDateTime regDt) { 
    	this.regDt = regDt; 
    }
    public LocalDateTime getUpdateDate() { 
    	return updateDate; 
    }
    public void setUpdateDate(LocalDateTime updateDate) { 
    	this.updateDate = updateDate; 
    }

}