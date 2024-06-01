package resume.resumegenerator.domain.entity;

public class IntroductionInfo extends BaseEntity {
    private String gpt;

    public IntroductionInfo() {
    }

    public IntroductionInfo(String gpt) {
        this.gpt = gpt;
    }

    public String getGpt() {
        return gpt;
    }

    public void setGpt(String gpt) {
        this.gpt = gpt;
    }
}
