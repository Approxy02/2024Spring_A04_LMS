//각 도서의 정보를 가지고 있는 객체

import java.util.ArrayList;

public class BookVO {
    private String title;
    private String author;
    private String addedDate;
    private int index;
    private String location;
    private BookRecord currentRecord; //현재 대여정보 -> null이면 대여중이 아님
    private ArrayList<BookRecord> bookRecords; //이전 대여정보들 -> null이면 대여기록 없음

    public BookVO(String title, String author, String addedDate, int index, String location) {
        this.title = title;
        this.author = author;
        this.addedDate = addedDate;
        this.index = index;
        this.location = location;
        this.currentRecord = null;
        this.bookRecords = null;
    }

    public BookVO(String title, String author, String addedDate, int index, String location, BookRecord currentRecord) {
        this.title = title;
        this.author = author;
        this.addedDate = addedDate;
        this.index = index;
        this.location = location;
        this.currentRecord = currentRecord;
        this.bookRecords = null;
    }

//    public BookVO(String title, String author, String addedDate, int index, String location, BookRecord currentRecord, ArrayList<BookRecord> bookRecords) {
//        this.title = title;
//        this.author = author;
//        this.addedDate = addedDate;
//        this.index = index;
//        this.location = location;
//        this.currentRecord = currentRecord;
//        this.bookRecords = bookRecords;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BookRecord getCurrentRecord() {
        return currentRecord;
    }

    public void setCurrentRecord(BookRecord currentRecord) {
        this.currentRecord = currentRecord;
    }

    public ArrayList<BookRecord> getBookRecords() {
        return bookRecords;
    }

    public void setBookRecords(ArrayList<BookRecord> bookRecords) {
        this.bookRecords = bookRecords;
    }

    public void addBookRecord(BookRecord bookRecord) {
        if (bookRecords == null) {
            bookRecords = new ArrayList<>();
        }
        bookRecords.add(bookRecord);
    }

    public void printBookInfo() {
        System.out.println("title : " + this.getTitle());
        System.out.println("author  : " + this.getAuthor());
        System.out.println("addedDate : " + this.getAddedDate());
        System.out.println("index : " + this.getIndex());
        System.out.println("location : " + this.getLocation());
        if (this.currentRecord != null) {
            System.out.println("startDate : " + this.currentRecord.getStartDate());
            System.out.println("endDate : " + this.currentRecord.getEndDate());
            System.out.println("studentNum : " + this.currentRecord.getStudentNum());
        }
        ArrayList<BookRecord> bookRecords = this.getBookRecords();
        if (bookRecords != null)
            for (BookRecord bookRecord : bookRecords) {
                System.out.println("대여정보");
                System.out.println("대여일 : " + bookRecord.getStartDate());
                System.out.println("반납일 : " + bookRecord.getEndDate());
                System.out.println("대여자 : " + bookRecord.getStudentNum());
            }
        System.out.println("------------------------------------------------------------");
    }

    public String toBookFileString(){
        String str = "";
        if(this.getCurrentRecord() == null){
            str += this.getTitle() + "/"+this.getAuthor()+"/"+this.getAddedDate()+"/"+this.getIndex()+"/"+this.getLocation();
        }else{ //not null
            str += this.getTitle() + "/"+this.getAuthor()+"/"+this.getAddedDate()+"/"+this.getIndex()+"/"+this.getLocation()+'/'+this.getCurrentRecord().getStartDate()+" ~ "+this.getCurrentRecord().getEndDate()+"/"+this.getCurrentRecord().getStudentNum();
        }
        return str;
    }

    //toBookFileString()에서 학번만 제외하고 출력
    public String toBookFileStringWithoutSno(){
        String str = "";
        if(this.getCurrentRecord() == null){
            str += this.getTitle() + "/"+this.getAuthor()+"/"+this.getAddedDate()+"/"+this.getIndex()+"/"+this.getLocation();
        }else{ //not null
            str += this.getTitle() + "/"+this.getAuthor()+"/"+this.getAddedDate()+"/"+this.getIndex()+"/"+this.getLocation()+'/'+this.getCurrentRecord().getStartDate()+" ~ "+this.getCurrentRecord().getEndDate();
        }
        return str;
    }
}
