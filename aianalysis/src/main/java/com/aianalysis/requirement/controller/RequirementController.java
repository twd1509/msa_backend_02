package com.aianalysis.requirement.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aianalysis.analysisresult.model.AnalysisResultVO;
import com.aianalysis.analysisresult.service.AnalysisResultService;
import com.aianalysis.requirement.model.RequirementVO;
import com.aianalysis.requirement.service.RequirementService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.AmazonRekognitionException;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.util.IOUtils;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class RequirementController {

	
	@Value("${file.path}")
	private String filePath;
	
    private final RequirementService rs;

    @Autowired
    public RequirementController(RequirementService rs) {
        this.rs = rs;
    }

    @Autowired
    public AnalysisResultService anlsRsltService;
    
    // 캐시
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(Files.readAllBytes(Paths.get(filePath + "/src/main/resources/static/uploads/" + filename)));
    }
    
    // 요청 사항 등록 (파일 업로드 포함)
    @PostMapping("/createRequirement")
    public String insertRequire(
        @RequestPart("file") MultipartFile file, 
        @RequestPart("requirement") RequirementVO reqVo) {

        if (file.isEmpty()) {
            return "파일이 비어 있습니다.";
        }

        try {
            // 허용된 이미지 확장자 목록
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");

            // 원본 파일명에서 확장자 분리
            String originalFilename = file.getOriginalFilename();
            String fileExtension = ""; // 파일 확장자
            String fileNameWithoutExt = originalFilename;

            int dotIndex = originalFilename.lastIndexOf(".");
            if (dotIndex > 0) {
                fileNameWithoutExt = originalFilename.substring(0, dotIndex); // 확장자 제외한 파일명
                fileExtension = originalFilename.substring(dotIndex).toLowerCase(); // 확장자 (소문자로 변환)
            }

            // 확장자가 허용된 파일인지 검사
            if (!allowedExtensions.contains(fileExtension)) {
                //System.out.println("허용되지 않은 파일 형식: " + fileExtension);
                return "허용되지 않은 파일 형식입니다. 업로드 가능한 확장자: " + allowedExtensions;
            }

            // 현재 날짜 및 시간을 yyyyMMdd_HHmmss 형식으로 포맷
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // 새로운 파일명: 기존 파일명 + 타임스탬프 + 확장자
            String newFilename = fileNameWithoutExt + "_" + timestamp + fileExtension;

            // 저장 경로 설정
            //String uploadDir = "C:\\kit\\workspace\\aiWorkspace\\aianalysis\\src\\main\\resources\\static\\uploads\\";
            
            String uploadDir = filePath + "/src/main/resources/static/uploads/";
            Path path = Paths.get(uploadDir + newFilename);
            Files.write(path, file.getBytes());

            //System.out.println("파일 저장 완료: " + path.toString());

            // ✅ 업로드 폴더 강제 리프레시 (React에서 즉시 반영되도록 처리)
            refreshUploadsDirectory(uploadDir);

            // 데이터베이스 저장
            reqVo.setFileTitle(newFilename);
            rs.insertRequirement(reqVo);
            //System.out.println("데이터베이스 저장 완료");

            return "파일 업로드 및 요청 등록 성공";
        } catch (IOException e) {
            //System.out.println("파일 업로드 실패: " + e.getMessage());
            return "파일 업로드 실패: " + e.getMessage();
        } catch (Exception e) {
            //System.out.println("예기치 않은 오류 발생: " + e.getMessage());
            return "예기치 않은 오류 발생: " + e.getMessage();
        }
    }



    // 특정 요청 사항 조회 (수정하기 위한 폼 제공)
    @GetMapping("/getUpdateForm")
    public List<RequirementVO> getForm(@RequestParam String reqNo) {
        return rs.selectRequirement(reqNo);
    }

    // 요청 사항 수정
    @PostMapping(value = "/updateRequirement", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateRequirement(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("reqNo") String reqNo,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("memId") String memId,
            @RequestPart("status") String status,
            @RequestPart("fileTitle") String fileTitle // 기존 파일명 받아오기
    ) {
        //System.out.println("📌 요청 사항 수정 시작");

        String newFilename = fileTitle; // 기본값을 기존 파일명으로 설정
        String uploadDir = filePath + "/src/main/resources/static/uploads/"; // 업로드 폴더 경로

        // 새로운 파일이 업로드된 경우 처리
        if (file != null && !file.isEmpty()) {
            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp");

            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            String fileNameWithoutExt = originalFilename;

            if (originalFilename != null) {
                int dotIndex = originalFilename.lastIndexOf(".");
                if (dotIndex > 0) {
                    fileNameWithoutExt = originalFilename.substring(0, dotIndex);
                    fileExtension = originalFilename.substring(dotIndex).toLowerCase();
                }
            }

            if (!allowedExtensions.contains(fileExtension)) {
                return ResponseEntity.badRequest().body("허용되지 않은 파일 형식입니다.");
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            newFilename = fileNameWithoutExt + "_" + timestamp + fileExtension;

            try {
                Path path = Paths.get(uploadDir + newFilename);
                Files.write(path, file.getBytes());

                // ✅ 업로드 폴더 강제 리프레시
                refreshUploadsDirectory(uploadDir);

            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 저장 중 오류 발생");
            }
        }

        // 데이터베이스 업데이트
        RequirementVO reqVo = new RequirementVO();
        reqVo.setReqNo(reqNo);
        reqVo.setTitle(title);
        reqVo.setContent(content);
        reqVo.setMemId(memId);
        reqVo.setStatus("대기중");
        reqVo.setFileTitle(newFilename);

        if (rs != null) {
            rs.updateRequirement(reqVo);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: 서비스 로직 호출 실패");
        }

        return ResponseEntity.ok("수정 완료");
    }

    /**
     * ✅ 업로드 폴더 강제 리프레시 함수
     * Spring의 정적 리소스를 갱신하여 새로 업로드된 파일을 즉시 반영할 수 있도록 한다.
     */
    private void refreshUploadsDirectory(String uploadDir) {
    	try {
            File directory = new File(uploadDir);
            if (directory.exists() && directory.isDirectory()) {
                
                // 더미 파일 생성 후 삭제하여 운영체제에서 변경 감지하도록 유도
                File dummyFile = new File(directory, "dummy_refresh.txt");
                Files.write(dummyFile.toPath(), "refresh".getBytes());
                dummyFile.delete();
            }
        } catch (Exception e) {
            //System.err.println("업로드 폴더 리프레시 중 오류 발생: " + e.getMessage());
        }
    }

    // 요청사항 삭제 API
    @DeleteMapping("/RequirementDel")
    public ResponseEntity<String> RequirementDel(@RequestParam String reqNo) {
        //System.out.println("📌 삭제 요청: " + reqNo);
        
        try {
            rs.requirementDel(reqNo);
            return ResponseEntity.ok("삭제 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제 실패: " + e.getMessage());
        }
    }

    // 요청 사항 조회 (관리자 및 일반 사용자)
    @GetMapping("/getRequirements")
    public List<RequirementVO> getAllRequirements(@RequestParam(required = false) String memId, @RequestParam String memGrade){
        if (memId == null || memId.isEmpty() || memGrade.equals("3")) {
        	// 관리자의 경우 전체 조회
            return rs.selectAllRequirements();
        } else { 
        	// 일반 사용자일 경우 본인 요청 조회
            return rs.selectRequirements(memId);
        }
    }
    
    @GetMapping("/getRequirementDetail")
    public List<RequirementVO> getRequirement(@RequestParam String reqNo){
    	return rs.selectRequirement(reqNo);
    }
    
    @GetMapping("/AiAnalysisReq")
    public void ai(@RequestParam String reqNo) throws Exception {
    	
    }

}
