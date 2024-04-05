public class BookRecord {
    private String startDate; //도서의 대여시작일
    private String endDate; //현재 대여정보의 경우 도서의 반납 예정일(대여기록의 경우 반납일)
    private String studentNum; //도서 대여자의 학번 8자리 숫자로 구성 => valid check 필요

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
