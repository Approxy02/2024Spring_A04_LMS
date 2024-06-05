import java.util.ArrayList;

public class Location {
    private String LocationName;
    private int BookStorageLimit;
    private int CurrentBookNum;

    private ArrayList<BookVO> BookList;

    public Location(String locationName, int bookStorageLimit){
        this.LocationName = locationName;
        this.BookStorageLimit = bookStorageLimit;
        this.CurrentBookNum = 0;
        this.BookList = null;
    }
    public Location(String locationName, int bookStorageLimit, int currentBookNum){
        this.LocationName = locationName;
        this.BookStorageLimit = bookStorageLimit;
        this.CurrentBookNum = currentBookNum;
        this.BookList = null;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String locationName) {
        LocationName = locationName;
    }

    public int getBookStorageLimit() {
        return BookStorageLimit;
    }

    public void setBookStorageLimit(int bookStorageLimit) {
        BookStorageLimit = bookStorageLimit;
    }

    public int getCurrentBookNum() {
        return CurrentBookNum;
    }

    public void setCurrentBookNum(int currentBookNum) {
        CurrentBookNum = currentBookNum;
    }

    public ArrayList<BookVO> getBookList() {
        return BookList;
    }

    public void setBookList(ArrayList<BookVO> bookList) {
        BookList = bookList;
    }

    public void addBookList(BookVO bookVO) {
        this.BookList.add(bookVO);
    }

    public String toLocationFileString(){
        String str = "";
        str += this.LocationName+"/"+this.BookStorageLimit+"/"+this.CurrentBookNum;

        return str;
    }



}


