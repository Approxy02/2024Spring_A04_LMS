public class BookRecord {
    private String startDate;
    private String endDate;
    private String studentNum;

    public BookRecord(String startDate, String endDate, String studentNum) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.studentNum = studentNum;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }
}
