package resume.resumegenerator.service;

import org.springframework.stereotype.Service;
import resume.resumegenerator.domain.entity.*;

import java.util.List;
import java.util.Map;

@Service
public class HtmlGeneratorService {

    public String generateHtml(Map<String, Object> resumeData) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"ko\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>12쉽소 이력서 출력</title>\n")
                .append("    <style>@import url(12style.css);</style>\n")
                .append("</head>\n")
                .append("<body>\n");

        // 인적사항
        PersonalInfo personalInfo = (PersonalInfo) resumeData.get("PersonalInfo");
        if (personalInfo != null) {
            html.append("    <div class=\"top\">\n")
                    .append("        <p class=\"logo\">12쉽소</p>\n")
                    .append("        <table class=\"topTable\">\n")
                    .append("            <tr>\n")
                    .append("                <td class=\"leftColumn\">\n")
                    .append("                    <p class=\"topHighlightText\" style=\"font-size:20px\">언제나 웃는 얼굴로!</p>\n")
                    .append("                    <p class=\"topHighlightText\" style=\"font-size:72px\">").append(personalInfo.getName()).append("</p>\n")
                    .append("                </td>\n")
                    .append("                <td class=\"rightColumn\"><img src=\"SampleImage.jpeg\" style=\"width:120px; height:160px\"></td>\n")
                    .append("            </tr>\n")
                    .append("        </table>\n")
                    .append("    </div>\n");
        }

        // 인적사항 상세
        if (personalInfo != null) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">인적사항</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <table class=\"infoTable\">\n");

            appendInfoRow(html, "생년월일", personalInfo.getBirth());
            appendInfoRow(html, "주소", personalInfo.getAddress());
            appendInfoRow(html, "연락처", personalInfo.getContact());
            appendInfoRow(html, "이메일", personalInfo.getEmail());

            html.append("        </table>\n")
                    .append("    </div>\n");
        }

        // 자기소개 - 성격 특징
        IntroductionInfo introductionInfo = (IntroductionInfo) resumeData.get("IntroductionInfo");
        if (introductionInfo != null && personalInfo != null) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">저, ").append(personalInfo.getName()).append("은</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <table class=\"infoTable\">\n");

            appendInfoRowCenter(html, introductionInfo.getPersonality1());
            appendInfoRowCenter(html, introductionInfo.getPersonality2());
            appendInfoRowCenter(html, introductionInfo.getPersonality3());

            html.append("        </table>\n")
                    .append("    </div>\n");
        }

        // 최종학력사항
        AcademicInfo academicInfo = (AcademicInfo) resumeData.get("AcademicInfo");
        if (academicInfo != null) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">최종학력사항</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <table class=\"infoTable\">\n");

            appendInfoRow(html, "학교명", academicInfo.getSchoolName());
            appendInfoRow(html, "전공", academicInfo.getDetailedMajor());
            appendInfoRow(html, "졸업연도", academicInfo.getGraduationDate());

            html.append("        </table>\n")
                    .append("    </div>\n");
        }

        // 자격증
        List<LicenseInfo> licenseInfos = (List<LicenseInfo>) resumeData.get("LicenseInfos");
        if (licenseInfos != null && !licenseInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">자격증</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <table class=\"infoTable\">\n");

            for (LicenseInfo licenseInfo : licenseInfos) {
                html.append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">자격증명</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(licenseInfo.getLicenseName()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">취득일</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(licenseInfo.getDate()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">발급기관</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(licenseInfo.getAgency()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr><td colspan=\"2\"><div class=\"infoLineSub\"></div><br></td></tr>\n");
            }

            html.append("        </table>\n")
                    .append("    </div>\n");
        }

        // 경력사항
        List<CareerInfo> careerInfos = (List<CareerInfo>) resumeData.get("CareerInfos");
        if (careerInfos != null && !careerInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">경력사항</span><div class=\"infoLine\"></div></div><br>\n");

            for (CareerInfo careerInfo : careerInfos) {
                html.append("        <p class=\"career\"><span class=\"roundBoxinText\">").append(careerInfo.getPlace()).append("</span>에서 ")
                        .append("<span class=\"roundBoxinText\">").append(careerInfo.getPeriod()).append("</span>동안 ")
                        .append("<span class=\"roundBoxinText\">").append(careerInfo.getTask()).append("</span> 업무를 맡았어요.</p><br>\n")
                        .append("        <div class=\"infoLineSub\"></div><br>\n");
            }

            html.append("    </div>\n");
        }

        // 훈련/교육
        List<TrainingInfo> trainingInfos = (List<TrainingInfo>) resumeData.get("TrainingInfos");
        if (trainingInfos != null && !trainingInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">훈련/교육</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <table class=\"infoTable\">\n");

            for (TrainingInfo trainingInfo : trainingInfos) {
                html.append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">훈련/교육명</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(trainingInfo.getTrainingName()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">훈련기간</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(trainingInfo.getDate()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr>\n")
                        .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">시행기관</p></div></td>\n")
                        .append("                <td class=\"infoRight\"><p class=\"infoText\">").append(trainingInfo.getAgency()).append("</p></td>\n")
                        .append("            </tr>\n")
                        .append("            <tr><td colspan=\"2\"><div class=\"infoLineSub\"></div><br></td></tr>\n");
            }

            html.append("        </table>\n")
                    .append("    </div>\n");
        }

        // 자기소개 내용
        if (introductionInfo != null) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\"><span class=\"infoTitle\">자기소개</span><div class=\"infoLine\"></div></div><br>\n")
                    .append("        <p class=\"paragraph\">").append(introductionInfo.getGpt()).append("</p>\n")
                    .append("    </div>\n");
        }

        // 마무리
        html.append("    <div class=\"infoBox\"><div class=\"infoLine\"></div></div>\n")
                .append("    <div class=\"infoBox\" style=\"text-align:center;\">\n")
                .append("        <p class=\"sloganEnd\">1,2,3처럼 쉬운 이력서 생성 도우미,</p>&nbsp;<p class=\"logoEnd\">12쉽소</p>\n")
                .append("    </div>\n")
                .append("</body>\n")
                .append("</html>");

        return html.toString();
    }

    private void appendInfoRow(StringBuilder html, String title, String content) {
        if (content != null && !content.isEmpty()) {
            html.append("            <tr>\n")
                    .append("                <td class=\"infoLeft\"><div class=\"roundBox\"><p class=\"infoText\">")
                    .append(title)
                    .append("</p></div></td>\n")
                    .append("                <td class=\"infoRight\"><p class=\"infoText\">")
                    .append(content)
                    .append("</p></td>\n")
                    .append("            </tr>\n");
        }
    }

    private void appendInfoRowCenter(StringBuilder html, String content) {
        if (content != null && !content.isEmpty()) {
            html.append("            <tr class=\"infoCenter\">\n")
                    .append("                <td><div class=\"roundBox\"><p class=\"infoText\">")
                    .append(content)
                    .append("</p></div></td>\n")
                    .append("            </tr>\n");
        }
    }
}
