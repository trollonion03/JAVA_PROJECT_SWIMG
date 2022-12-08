package project.hekim;

public class Word {
    String eng;
    String kor;

    public Word(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    @Override
    public String toString() {
        return eng+" : "+kor;
    }
}
