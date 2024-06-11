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
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\">\n")
                .append("    <title>12쉽소 이력서 출력 A4</title>\n")
                .append("    <link rel=\"stylesheet\" href=\"12style_A4.css\">\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <button id=\"topdfButton\" onclick=\"downloadPDF()\" data-html2canvas-ignore=\"true\">PDF로 다운로드</button>\n");

        // 페이지 1 시작
        html.append("    <section class=\"page\">\n")
                .append("        <div id=\"topBackground\"><p id=\"logo\">12쉽소</p></div>\n")
                .append("        <table class=\"topTable\">\n")
                .append("            <tr>\n")
                .append("                <td class=\"leftColumn\">\n")
                .append("                    <img src=\"SampleImage.jpeg\" style=\"width:120px; height:160px;\">\n")
                .append("                </td>\n")
                .append("                <td class=\"centerColumn\">\n")
                .append("                    <div class=\"topHighlightTextSmall\">언제나 웃는 얼굴로!</div>\n")
                .append("                    <div class=\"topHighlightTextLarge\">");

        // 인적사항
        PersonalInfo personalInfo = (PersonalInfo) resumeData.get("PersonalInfo");
        if (personalInfo != null) {
            html.append(personalInfo.getName());
        }

        html.append("</div>\n")
                .append("                </td>\n")
                .append("                <td class=\"rightColumn\">\n");

        // 성격 특징을 한 줄에 하나씩
        IntroductionInfo introductionInfo = (IntroductionInfo) resumeData.get("IntroductionInfo");
        if (introductionInfo != null) {
            appendPersonality(html, introductionInfo.getPersonality1());
            appendPersonality(html, introductionInfo.getPersonality2());
            appendPersonality(html, introductionInfo.getPersonality3());
        }

        html.append("                </td>\n")
                .append("            </tr>\n")
                .append("        </table>\n")
                .append("        <div class=\"infoBox\"><div class=\"infoLine\" style=\"border-top: 4px solid #001ED6;\"></div></div>\n")
                .append("        <div class=\"infoBox\">\n")
                .append("            <table class=\"infoTable\">\n")
                .append("                <tr>\n")
                .append("                    <td class=\"leftInfoSide\">인적사항</td>\n")
                .append("                    <td class=\"rightInfoSide\">\n")
                .append("                        <table class=\"infoTable\">\n");

        // 인적사항 상세
        if (personalInfo != null) {
            appendDetailedInfo(html, "이름", personalInfo.getName());
            appendDetailedInfo(html, "주소", personalInfo.getAddress());
            appendDetailedInfo(html, "연락처", personalInfo.getContact());
            appendDetailedInfo(html, "이메일", personalInfo.getEmail());
        }

        html.append("                        </table>\n")
                .append("                    </td>\n")
                .append("                </tr>\n")
                .append("            </table>\n")
                .append("        </div>\n");

        // 최종학력사항
        AcademicInfo academicInfo = (AcademicInfo) resumeData.get("AcademicInfo");
        if (academicInfo != null) {
            html.append("        <div class=\"infoBox\">\n")
                    .append("            <table class=\"infoTable\">\n")
                    .append("                <tr>\n")
                    .append("                    <td class=\"leftInfoSide\">최종<br>학력사항</td>\n")
                    .append("                    <td class=\"rightInfoSide\">\n")
                    .append("                        <table class=\"infoTable\">\n");

            appendDetailedInfo(html, "학교명", academicInfo.getSchoolName());
            appendDetailedInfo(html, "전공", academicInfo.getDetailedMajor());
            appendDetailedInfo(html, "졸업연도", academicInfo.getGraduationDate());

            html.append("                        </table>\n")
                    .append("                    </td>\n")
                    .append("                </tr>\n")
                    .append("            </table>\n")
                    .append("        </div>\n");
        }

        // 자격증
        List<LicenseInfo> licenseInfos = (List<LicenseInfo>) resumeData.get("LicenseInfos");
        if (licenseInfos != null && !licenseInfos.isEmpty()) {
            html.append("        <div class=\"infoBox\">\n")
                    .append("            <table class=\"infoTable\">\n")
                    .append("                <tr>\n")
                    .append("                    <td class=\"leftInfoSide\">자격증</td>\n")
                    .append("                    <td class=\"rightInfoSide\">\n")
                    .append("                        <table class=\"infoTable\">\n");

            for (LicenseInfo licenseInfo : licenseInfos) {
                appendDetailedInfo(html, "자격증명", licenseInfo.getLicenseName());
                appendDetailedInfo(html, "취득일", licenseInfo.getDate());
                appendDetailedInfo(html, "발급기관", licenseInfo.getAgency());
                html.append("                            <tr><td style=\"padding-top:5px; padding-bottom:5px;\" colspan=\"2\"><div class=\"infoLineSub\"></div></td></tr>\n");
            }

            html.append("                        </table>\n")
                    .append("                    </td>\n")
                    .append("                </tr>\n")
                    .append("            </table>\n")
                    .append("        </div>\n");
        }

        html.append("    </section>\n");

        // 페이지 2 시작
        html.append("    <section class=\"page\">\n")
                .append("        <div id=\"topBackground2p\"><p id=\"logo\">12쉽소</p></div>\n")
                .append("        <br><br><br><br>\n");

        // 경력사항
        List<CareerInfo> careerInfos = (List<CareerInfo>) resumeData.get("CareerInfos");
        if (careerInfos != null && !careerInfos.isEmpty()) {
            html.append("        <div class=\"infoBox\">\n")
                    .append("            <table class=\"infoTable\">\n")
                    .append("                <tr>\n")
                    .append("                    <td class=\"leftInfoSide\">경력사항</td>\n")
                    .append("                    <td class=\"rightInfoSide\">\n")
                    .append("                        <table>\n");

            for (CareerInfo careerInfo : careerInfos) {
                html.append("                            <tr>\n")
                        .append("                                <td>\n")
                        .append("                                    <p class=\"career\"><span class=\"roundBoxinText\">")
                        .append(careerInfo.getPlace()).append("</span> <span class=\"roundBoxinText\">")
                        .append(careerInfo.getPeriod()).append("</span>동안 <span class=\"roundBoxinText\">")
                        .append(careerInfo.getTask()).append("</span> 업무를 맡았어요.</p>\n")
                        .append("                                </td>\n")
                        .append("                            </tr>\n")
                        .append("                            <tr><td style=\"padding-top:5px; padding-bottom:5px;\" colspan=\"2\"><div class=\"infoLineSub\"></div></td></tr>\n");
            }

            html.append("                        </table>\n")
                    .append("                    </td>\n")
                    .append("                </tr>\n")
                    .append("            </table>\n")
                    .append("        </div>\n");
        }

        // 훈련/교육 사항 추가
        List<TrainingInfo> trainingInfos = (List<TrainingInfo>) resumeData.get("TrainingInfos");
        if (trainingInfos != null && !trainingInfos.isEmpty()) {
            html.append("        <div class=\"infoBox\">\n")
                    .append("            <table class=\"infoTable\">\n")
                    .append("                <tr>\n")
                    .append("                    <td class=\"leftInfoSide\">훈련/교육</td>\n")
                    .append("                    <td class=\"rightInfoSide\">\n")
                    .append("                        <table class=\"infoTable\">\n");

            for (TrainingInfo trainingInfo : trainingInfos) {
                appendDetailedInfo(html, "훈련/교육명", trainingInfo.getTrainingName());
                appendDetailedInfo(html, "훈련기간", trainingInfo.getDate());
                appendDetailedInfo(html, "시행기관", trainingInfo.getAgency());
                html.append("                            <tr><td style=\"padding-top:5px; padding-bottom:5px;\" colspan=\"2\"><div class=\"infoLineSub\"></div></td></tr>\n");
            }

            html.append("                        </table>\n")
                    .append("                    </td>\n")
                    .append("                </tr>\n")
                    .append("            </table>\n")
                    .append("        </div>\n");
        }

        // 자기소개 내용
        if (introductionInfo != null) {
            html.append("        <div class=\"infoBox\">\n")
                    .append("            <table class=\"infoTable\">\n")
                    .append("                <tr>\n")
                    .append("                    <td class=\"leftInfoSide\">자기소개</td>\n")
                    .append("                    <td class=\"rightInfoSide\">\n")
                    .append("                        <p class=\"paragraph\">").append(introductionInfo.getGpt()).append("</p>\n")
                    .append("                    </td>\n")
                    .append("                </tr>\n")
                    .append("            </table>\n")
                    .append("        </div>\n")
                    .append("        <p id=\"signature\"> 상기 내용이 <span id=\"eb\">사실임을 확인</span>합니다.&nbsp &nbsp &nbsp ")
                    .append(personalInfo != null ? personalInfo.getName() : "")
                    .append(" &nbsp &nbsp (서명) </p>\n");
        }

        html.append("    </section>\n")
                .append("</body>\n")
                .append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.3.1/jspdf.umd.min.js\"></script>\n")
                .append("<script src=\"https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.3.2/html2canvas.min.js\"></script>\n")
                .append("<script>\n")
                .append("    async function downloadPDF() {\n")
                .append("        const pages = document.querySelectorAll('.page');\n")
                .append("        const doc = new jspdf.jsPDF('p', 'mm', 'a4');\n")
                .append("        for (let i = 0; i < pages.length; i++) {\n")
                .append("            const canvas = await html2canvas(pages[i], { scale: 1, useCORS: true });\n")
                .append("            const imgData = canvas.toDataURL('SampleImage.jpeg');\n")
                .append("            const imgWidth = 210;\n")
                .append("            const pageHeight = 297;\n")
                .append("            const imgHeight = canvas.height * imgWidth / canvas.width;\n")
                .append("            if (i > 0) {\n")
                .append("                doc.addPage();\n")
                .append("            }\n")
                .append("            doc.addImage(imgData, 'PNG', 0, 0, imgWidth, imgHeight);\n")
                .append("        }\n")
                .append("        doc.save('resume.pdf');\n")
                .append("    }\n")
                .append("</script>\n")
                .append("</html>");

        return html.toString();
    }

    private void appendDetailedInfo(StringBuilder html, String title, String content) {
        if (content != null && !content.isEmpty()) {
            html.append("                            <tr>\n")
                    .append("                                <td class=\"rightLeftInfo\">\n")
                    .append("                                    <div class=\"roundBox\">").append(title).append("</div>\n")
                    .append("                                </td>\n")
                    .append("                                <td class=\"rightRightInfo\">").append(content).append("</td>\n")
                    .append("                            </tr>\n");
        }
    }

    private void appendPersonality(StringBuilder html, String content) {
        if (content != null && !content.isEmpty()) {
            html.append("                    <div class=\"roundBoxinRow\" style=\"margin-bottom : 8px;\"><p class=\"infoText\">").append(content).append("</p></div>\n");
        }
    }
}
