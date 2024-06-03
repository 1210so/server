package resume.resumegenerator.domain.entity;

public class IntroductionInfo extends BaseEntity {
    private String gpt;
    private String personality1;
    private String personality2;
    private String personality3;

    public IntroductionInfo() {
    }

    public IntroductionInfo(String gpt, String personality1, String personality2, String personality3) {
        this.gpt = gpt;
        this.personality1 = personality1;
        this.personality2 = personality2;
        this.personality3 = personality3;
    }

    public String getGpt() {
        return gpt;
    }

    public void setGpt(String gpt) {
        this.gpt = gpt;
    }

    public String getPersonality1() {
        return personality1;
    }

    public void setPersonality1(String personality1) {
        this.personality1 = personality1;
    }

    public String getPersonality2() {
        return personality2;
    }

    public void setPersonality2(String personality2) {
        this.personality2 = personality2;
    }

    public String getPersonality3() {
        return personality3;
    }

    public void setPersonality3(String personality3) {
        this.personality3 = personality3;
    }
}

