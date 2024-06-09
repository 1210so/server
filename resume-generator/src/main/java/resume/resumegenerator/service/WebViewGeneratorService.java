package resume.resumegenerator.service;

import org.springframework.stereotype.Service;
import resume.resumegenerator.domain.entity.*;

import java.util.List;
import java.util.Map;

@Service
public class WebViewGeneratorService {

    public String generateWebView(Map<String, Object> resumeData) {
        String name = getStringValue(((PersonalInfo) resumeData.get("PersonalInfo")).getName());

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html lang=\"ko\">\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n")
                .append("    <title>")
                .append(name)
                .append("님의 이력서 - 12쉽소</title>\n")
                .append("    <style>\n")
                .append("        @import url(12style.css);\n")
                .append("    </style>\n")
                .append("    <meta property=\"og:title\" content=\"")
                .append(name)
                .append("님의 이력서 바로가기!\">\n")
                .append("    <meta property=\"og:type\" content=\"website\">\n")
                .append("    <meta property=\"og:image\" content=\"header.png\">\n")
                .append("    <meta property=\"og:description\" content=\"1, 2, 3처럼 쉬운 이력서 만들기, 12쉽소로 생성된 이력서입니다.\">\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <div class=\"top\"> \n")
                .append("        <p class=\"logo\">12쉽소</p> \n")
                .append("        <table class=\"topTable\">\n")
                .append("            <tr>\n")
                .append("                <td class=\"leftColumn\"> \n")
                .append("                    <p class=\"topHighlightText\" style=\"font-size:20px\">언제나 웃는 얼굴로!</p> \n")
                .append("                    <p class=\"topHighlightText\" style=\"font-size:72px\">")
                .append(name)
                .append("</p> \n")
                .append("                </td>\n")
                .append("                <td class=\"rightColumn\"> \n")
                .append("                    <img src=\"b.png\" style=\"width:120px; height:160px\">\n")
                .append("                </td>\n")
                .append("            </tr>\n")
                .append("        </table>\n")
                .append("    </div> \n")
                .append("    <div class=\"infoBox\">\n")
                .append("        <div class=\"infoTitleContainer\">\n")
                .append("            <span class=\"infoTitle\">인적사항</span>\n")
                .append("            <div class=\"infoLine\"></div>\n")
                .append("        </div>\n")
                .append("        <br>\n")
                .append("        <table class=\"infoTable\">\n");

        // PersonalInfo
        PersonalInfo personalInfo = (PersonalInfo) resumeData.get("PersonalInfo");
        if (personalInfo != null) {
            if (personalInfo.getBirth() != null) {
                appendDetailedInfo(html, "생년월일", getStringValue(personalInfo.getBirth()));
            }
            if (personalInfo.getAddress() != null) {
                appendDetailedInfo(html, "주소", getStringValue(personalInfo.getAddress()));
            }
            if (personalInfo.getContact() != null) {
                appendDetailedInfo(html, "연락처", getStringValue(personalInfo.getContact()));
            }
            if (personalInfo.getEmail() != null) {
                appendDetailedInfo(html, "이메일", getStringValue(personalInfo.getEmail()));
            }
        }

        html.append("        </table>\n")
                .append("    </div>\n")
                .append("    <br>\n")
                .append("    <div class=\"infoBox\">\n")
                .append("        <div class=\"infoTitleContainer\">\n")
                .append("            <span class=\"infoTitle\">저는요, </span>\n")
                .append("            <div class=\"infoLine\"></div>\n")
                .append("        </div>\n")
                .append("        <br>\n")
                .append("        <table class=\"infoTable\">\n");

        // IntroductionInfo
        IntroductionInfo introductionInfo = (IntroductionInfo) resumeData.get("IntroductionInfo");
        if (introductionInfo != null) {
            appendPersonality(html, introductionInfo.getPersonality1());
            appendPersonality(html, introductionInfo.getPersonality2());
            appendPersonality(html, introductionInfo.getPersonality3());
        }

        html.append("        </table>\n")
                .append("    </div>\n")
                .append("    <br>\n");

        // 최종학력사항
        AcademicInfo academicInfo = (AcademicInfo) resumeData.get("AcademicInfo");
        if (academicInfo != null) {
            boolean hasSchoolName = academicInfo.getSchoolName() != null && !academicInfo.getSchoolName().isEmpty();
            boolean hasDetailedMajor = academicInfo.getDetailedMajor() != null && !academicInfo.getDetailedMajor().isEmpty();
            boolean hasGraduationDate = academicInfo.getGraduationDate() != null && !academicInfo.getGraduationDate().isEmpty();

            if (hasSchoolName || hasDetailedMajor || hasGraduationDate) {
                html.append("    <div class=\"infoBox\">\n")
                        .append("        <div class=\"infoTitleContainer\">\n")
                        .append("            <span class=\"infoTitle\">최종학력사항</span>\n")
                        .append("            <div class=\"infoLine\"></div>\n")
                        .append("        </div>\n")
                        .append("        <br>\n")
                        .append("        <table class=\"infoTable\">\n");

                if (hasSchoolName) {
                    appendDetailedInfo(html, "학교명", getStringValue(academicInfo.getSchoolName()));
                }
                if (hasDetailedMajor) {
                    appendDetailedInfo(html, "전공", getStringValue(academicInfo.getDetailedMajor()));
                }
                if (hasGraduationDate) {
                    appendDetailedInfo(html, "졸업연도", getStringValue(academicInfo.getGraduationDate()));
                }

                html.append("        </table>\n")
                        .append("    </div>\n")
                        .append("    <br>\n");
            }
        }


        // 자격증
        List<LicenseInfo> licenseInfos = (List<LicenseInfo>) resumeData.get("LicenseInfos");
        if (licenseInfos != null && !licenseInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\">\n")
                    .append("            <span class=\"infoTitle\">자격증</span>\n")
                    .append("            <div class=\"infoLine\"></div>\n")
                    .append("        </div>\n")
                    .append("        <br>\n")
                    .append("        <table class=\"infoTable\">\n");

            for (int i = 0; i < licenseInfos.size(); i++) {
                LicenseInfo license = licenseInfos.get(i);
                appendLicenseInfo(html, license);
                if (i < licenseInfos.size() - 1) {
                    html.append("        <tr><td style=\"padding-top:5px; padding-bottom:5px;\" colspan=\"2\"><div class=\"infoLineSub\"></div></td></tr>\n");
                }
            }

            html.append("        </table>\n")
                    .append("    </div>\n")
                    .append("    <br>\n");
        }

        // 교육사항
        List<TrainingInfo> trainingInfos = (List<TrainingInfo>) resumeData.get("TrainingInfos");
        if (trainingInfos != null && !trainingInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\">\n")
                    .append("            <span class=\"infoTitle\">훈련/교육사항</span>\n")
                    .append("            <div class=\"infoLine\"></div>\n")
                    .append("        </div>\n")
                    .append("        <br>\n")
                    .append("        <table class=\"infoTable\">\n");

            for (int i = 0; i < trainingInfos.size(); i++) {
                TrainingInfo training = trainingInfos.get(i);
                appendTrainingInfo(html, training);
                if (i < trainingInfos.size() - 1) {
                    html.append("        <tr><td style=\"padding-top:5px; padding-bottom:5px;\" colspan=\"2\"><div class=\"infoLineSub\"></div></td></tr>\n");
                }
            }

            html.append("        </table>\n")
                    .append("    </div>\n")
                    .append("    <br>\n");
        }

        // 경력사항
        List<CareerInfo> careerInfos = (List<CareerInfo>) resumeData.get("CareerInfos");
        if (careerInfos != null && !careerInfos.isEmpty()) {
            html.append("    <div class=\"infoBox\">\n")
                    .append("        <div class=\"infoTitleContainer\">\n")
                    .append("            <span class=\"infoTitle\">경력사항</span>\n")
                    .append("            <div class=\"infoLine\"></div>\n")
                    .append("        </div>\n")
                    .append("        <br>\n");

            for (int i = 0; i < careerInfos.size(); i++) {
                CareerInfo career = careerInfos.get(i);
                html.append("        <p class=\"career\"> <span class=\"roundBoxinText\">")
                        .append(getStringValue(career.getPlace()))
                        .append("</span>에서 <span class=\"roundBoxinText\">")
                        .append(getStringValue(career.getPeriod()))
                        .append("</span>동안 <span class=\"roundBoxinText\">")
                        .append(getStringValue(career.getTask()))
                        .append("</span> 업무를 맡았어요. </p>\n")
                        .append("        <br>\n");
                if (i < careerInfos.size() - 1) {
                    html.append("        <div class=\"infoLineSub\"></div>\n")
                            .append("        <br>\n");
                }
            }

            html.append("    </div>\n")
                    .append("    <br>\n");
        }

        // 자기소개
        html.append("    <div class=\"infoBox\">\n")
                .append("        <div class=\"infoTitleContainer\">\n")
                .append("            <span class=\"infoTitle\">자기소개</span>\n")
                .append("            <div class=\"infoLine\"></div>\n")
                .append("        </div>\n")
                .append("        <br>\n")
                .append("        <p class=\"paragraph\">")
                .append(getStringValue(((IntroductionInfo) resumeData.get("IntroductionInfo")).getGpt()))
                .append("</p>\n")
                .append("    </div>\n")
                .append("    <br>\n")
                .append("    <div class=\"infoBox\"><div class=\"infoLine\"></div></div>\n")
                .append("    <div class=\"infoBox\" style=\"text-align:center;\">\n")
                .append("        <p class=\"sloganEnd\">1,2,3처럼 쉬운 이력서 생성 도우미,</p>&nbsp;<p class=\"logoEnd\">12쉽소</p> \n")
                .append("    </div>\n")
                .append("</body>\n")
                .append("</html>");

        return html.toString();
    }

    private void appendLicenseInfo(StringBuilder html, LicenseInfo license) {
        html.append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">자격증명</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(license.getLicenseName()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n")
                .append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">취득일</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(license.getDate()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n")
                .append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">발급기관</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(license.getAgency()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n");
    }

    private void appendTrainingInfo(StringBuilder html, TrainingInfo training) {
        html.append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">훈련/교육명</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(training.getTrainingName()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n")
                .append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">훈련기간</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(training.getDate()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n")
                .append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">시행기관</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(getStringValue(training.getAgency()))
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n");
    }

    private void appendPersonality(StringBuilder html, Object personality) {
        if (personality != null) {
            html.append("            <tr class=\"infoCenter\"> \n")
                    .append("                <td> <div class=\"roundBox\"><p class=\"infoText\">")
                    .append(getStringValue(personality))
                    .append("</p></div> </td>\n")
                    .append("            </tr>\n");
        }
    }

    private void appendDetailedInfo(StringBuilder html, String title, String content) {
        html.append("            <tr>\n")
                .append("                <td class=\"infoLeft\"> \n")
                .append("                    <div class=\"roundBox\"><p class=\"infoText\">")
                .append(title)
                .append("</p></div>\n")
                .append("                </td>\n")
                .append("                <td class=\"infoRight\"> \n")
                .append("                    <p class=\"infoText\">")
                .append(content)
                .append("</p>\n")
                .append("                </td>\n")
                .append("            </tr>\n");
    }

    private String getStringValue(Object value) {
        return value == null ? "" : value.toString();
    }
}
