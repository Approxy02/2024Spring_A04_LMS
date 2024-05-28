import java.sql.SQLOutput;
import java.util.ArrayList;

public class User {
    private String studentNum;
    private int isPenalty;
    private String penaltyDate;
    private ArrayList<BookVO> current_borrowed_books;
    private ArrayList<BookVO> previous_borrowed_books;

    public User(String studentNum, int isPenalty, String penaltyDate) {
        this.studentNum = studentNum;
        this.isPenalty = isPenalty;
        this.penaltyDate = penaltyDate;
        this.current_borrowed_books = new ArrayList<>();
        this.previous_borrowed_books = new ArrayList<>();
    }

    public String getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum;
    }

    public int getIsPenalty() {
        return isPenalty;
    }

    public void setIsPenalty(int isPenalty) {
        this.isPenalty = isPenalty;
    }

    public String getPenaltyDate() {
        return penaltyDate;
    }

    public void setPenaltyDate(String penaltyDate) {
        this.penaltyDate = penaltyDate;
    }

    public ArrayList<BookVO> getCurrentBorrowedBooks() {
        return current_borrowed_books;
    }

    public void setCurrentBorrowedBooks(ArrayList<BookVO> current_borrowed_books) {
        this.current_borrowed_books = current_borrowed_books;
    }

    public ArrayList<BookVO> getPreviousBorrowedBooks() {
        return previous_borrowed_books;
    }

    public void setPreviousBorrowedBooks(ArrayList<BookVO> previous_borrowed_books) {
        this.previous_borrowed_books = previous_borrowed_books;
    }

    public void addCurrentBorrowedBook(BookVO bookvo) {
        this.current_borrowed_books.add(bookvo);
    }

    public void addPreviousBorrowedBook(BookVO bookvo) {
        this.previous_borrowed_books.add(bookvo);
    }

    public void printUser() {
        System.out.println("학번 : " + this.getStudentNum());
        System.out.println("연체 여부 : " + this.getIsPenalty());
        System.out.println("연체 날짜 : " + this.getPenaltyDate());
        ArrayList<BookVO> current_borrowed_books = this.getCurrentBorrowedBooks();
        if (current_borrowed_books != null) {
            System.out.println("현재 대여중인 도서");
            for (BookVO book : current_borrowed_books) {
                book.printBookInfo();
            }
        }
        ArrayList<BookVO> previous_borrowed_books = this.getPreviousBorrowedBooks();
        if (previous_borrowed_books != null) {
            System.out.println("이전 대여 도서");
            for (BookVO book : previous_borrowed_books) {
                book.printBookInfo();
            }
        }
    }
}
