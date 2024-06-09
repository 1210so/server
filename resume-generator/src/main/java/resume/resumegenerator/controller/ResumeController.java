package resume.resumegenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import resume.resumegenerator.domain.entity.*;
import resume.resumegenerator.gpt.service.ChatGptService;
import resume.resumegenerator.service.GitHubService;
import resume.resumegenerator.service.HtmlGeneratorService;
import resume.resumegenerator.service.WebViewGeneratorService;
import resume.resumegenerator.store.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final PersonalInfoStore personalInfoStore;
    private final AcademicInfoStore academicInfoStore;
    private final CareerInfoStore careerInfoStore;
    private final LicenseInfoStore licenseInfoStore;
    private final TrainingInfoStore trainingInfoStore;
    private final IntroductionInfoStore introductionInfoStore;
    private final HtmlGeneratorService htmlGeneratorService;
    private final WebViewGeneratorService webViewGeneratorService;
    private final GitHubService gitHubService;
    private final ChatGptService chatGptService;

    @Autowired
    public ResumeController(PersonalInfoStore personalInfoStore, AcademicInfoStore academicInfoStore,
                            CareerInfoStore careerInfoStore, LicenseInfoStore licenseInfoStore,
                            TrainingInfoStore trainingInfoStore, IntroductionInfoStore introductionInfoStore,
                            HtmlGeneratorService htmlGeneratorService, WebViewGeneratorService webViewGeneratorService, GitHubService gitHubService,
                            ChatGptService chatGptService) {
        this.personalInfoStore = personalInfoStore;
        this.academicInfoStore = academicInfoStore;
        this.careerInfoStore = careerInfoStore;
        this.licenseInfoStore = licenseInfoStore;
        this.trainingInfoStore = trainingInfoStore;
        this.introductionInfoStore = introductionInfoStore;
        this.htmlGeneratorService = htmlGeneratorService;
        this.webViewGeneratorService = webViewGeneratorService;
        this.gitHubService = gitHubService;
        this.chatGptService = chatGptService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getCompleteResume(@PathVariable Long userId) {
        Map<String, Object> resume = new HashMap<>();
        PersonalInfo personalInfo = personalInfoStore.findById(userId);
        AcademicInfo academicInfo = academicInfoStore.findById(userId);
        List<CareerInfo> careerInfos = careerInfoStore.findById(userId);
        List<LicenseInfo> licenseInfos = licenseInfoStore.findById(userId);
        List<TrainingInfo> trainingInfos = trainingInfoStore.findById(userId);
        IntroductionInfo introductionInfo = introductionInfoStore.findById(userId);

        if (personalInfo != null) {
            resume.put("PersonalInfo", personalInfo);
        }
        if (academicInfo != null) {
            resume.put("AcademicInfo", academicInfo);
        }
        if (careerInfos != null) {
            resume.put("CareerInfos", careerInfos);
        }
        if (licenseInfos != null) {
            resume.put("LicenseInfos", licenseInfos);
        }
        if (trainingInfos != null) {
            resume.put("TrainingInfos", trainingInfos);
        }
        if (introductionInfo != null) {
            resume.put("IntroductionInfo", introductionInfo);
        }
        if (resume.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(resume);
    }

    @PostMapping("/{userId}/uploadView")
    public ResponseEntity<String> uploadResumeEmptyToGitHub(@PathVariable Long userId) {
        try {
            Map<String, Object> resume = new HashMap<>();
            PersonalInfo personalInfo = personalInfoStore.findById(userId);
            AcademicInfo academicInfo = academicInfoStore.findById(userId);
            List<CareerInfo> careerInfos = careerInfoStore.findById(userId);
            List<LicenseInfo> licenseInfos = licenseInfoStore.findById(userId);
            List<TrainingInfo> trainingInfos = trainingInfoStore.findById(userId);
            IntroductionInfo introductionInfo = introductionInfoStore.findById(userId);

            if (personalInfo != null) {
                resume.put("PersonalInfo", personalInfo);
            }
            if (academicInfo != null) {
                resume.put("AcademicInfo", academicInfo);
            }
            if (careerInfos != null) {
                resume.put("CareerInfos", careerInfos);
            }
            if (licenseInfos != null) {
                resume.put("LicenseInfos", licenseInfos);
            }
            if (trainingInfos != null) {
                resume.put("TrainingInfos", trainingInfos);
            }
            if (introductionInfo != null) {
                resume.put("IntroductionInfo", introductionInfo);
            }
            if (resume.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // HTML 생성
            String webviewHtml = webViewGeneratorService.generateWebView(resume);

            // GitHub 업로드
            String fileName = "emptyResume_" + userId + "_" + System.currentTimeMillis() + ".html";
            String fileUrl = "https://1210so.github.io/resumeMobileHTML/" + fileName;
            gitHubService.uploadFileToGitHub(fileName, webviewHtml);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

    @PostMapping("/{userId}/uploadPdf")
    public ResponseEntity<String> uploadResumeToGitHub(@PathVariable Long userId) {
        try {
            Map<String, Object> resume = new HashMap<>();
            PersonalInfo personalInfo = personalInfoStore.findById(userId);
            AcademicInfo academicInfo = academicInfoStore.findById(userId);
            List<CareerInfo> careerInfos = careerInfoStore.findById(userId);
            List<LicenseInfo> licenseInfos = licenseInfoStore.findById(userId);
            List<TrainingInfo> trainingInfos = trainingInfoStore.findById(userId);
            IntroductionInfo introductionInfo = introductionInfoStore.findById(userId);

            if (personalInfo != null) {
                resume.put("PersonalInfo", personalInfo);
            }
            if (academicInfo != null) {
                resume.put("AcademicInfo", academicInfo);
            }
            if (careerInfos != null) {
                resume.put("CareerInfos", careerInfos);
            }
            if (licenseInfos != null) {
                resume.put("LicenseInfos", licenseInfos);
            }
            if (trainingInfos != null) {
                resume.put("TrainingInfos", trainingInfos);
            }
            if (introductionInfo != null) {
                resume.put("IntroductionInfo", introductionInfo);
            }
            if (resume.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // HTML 생성
            String htmlContent = htmlGeneratorService.generateHtml(resume);

            // GitHub 업로드
            String fileName = "resume_" + userId + "_" + System.currentTimeMillis() + ".html";
            String fileUrl = "https://1210so.github.io/resumeMobileHTML/" + fileName;
            gitHubService.uploadFileToGitHub(fileName, htmlContent);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }
}

