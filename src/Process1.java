//각 process는 생성자를 통해 오늘 날짜(todayDate)를 받아온다.
//todayDate는 사용자가 입력한 날짜를 의미하며, 각 년도, 월, 일은 공백으로 구분 (EX. 2023 02 01)
//모든 Process는 BookDAO를 통해 파일에서 데이터를 읽어오고, 데이터를 파일에 쓴다.
//BookDAO의 getDataFromFiles() 메소드는 bookList.txt 파일에서 데이터를 읽어와서 ArrayList<BookVO> 형태로 반환한다.
//ArrayList<BookVO>를 통해서 각 도서에 접근하고, 도서 정보를 수정 할 수 있다.
//각 Process는 특정 작업을 마치면, BookDAO의 writeDataToFiles(ArrayList<BookVO> bookList) 메소드를 호출하여, 변경된 데이터를 txt파일에 기록한다.

import java.util.ArrayList;
import java.util.Scanner;

public class Process1 {    //도서 추가 기능
    Scanner scanner;
    private String todayDate;
    private ArrayList<BookVO> booklist;
    private BookDAO bookDAO = new BookDAO();
    private String process_input;

    public Process1(String todayDate) {
        this.todayDate = todayDate;
        booklist = bookDAO.getDataFromFiles();
        addBook();
    }

    public void addBook() {
        String title;
        String author;
        String location;
        int index;

        title = getTitle();
        author = getAuthor();
        location = getLocation();
        index = getIndex(title, author);
        booklist.add(new BookVO(title, author, todayDate, index, location));
        bookDAO.writeDataToFiles(booklist, todayDate);
        System.out.println("도서 등록이 완료되었습니다.");
        System.out.println(title + " / " + author + " / " + todayDate + " / " + index + " / " + location);
        System.out.println("------------------------------------------------------------");
    }

    private String getTitle() {
        String title;
        while(true) {
            System.out.print("\"도서 제목\"을 입력하세요 > ");
            scanner = new Scanner(System.in);
            title = scanner.nextLine();
            if(title.trim().isBlank()) {
                System.out.println("------------------------------------------------------------");
                continue;
            }
            else {
                System.out.println("------------------------------------------------------------");
                return title.trim();
            }
        }
    }

    private String getAuthor() {
        String author;
        while(true) {
            System.out.print("\"도서 저자\"을 입력하세요 > ");
            scanner = new Scanner(System.in);
            author = scanner.nextLine();
            if(author.trim().isBlank()) {
                System.out.println("------------------------------------------------------------");
                continue;
            }
            else {
                System.out.println("------------------------------------------------------------");
                return author.trim();
            }
        }
    }

    private String getLocation() {
        System.out.println("현재 자료실 정보");
        System.out.println("1) 어린이 도서실");
        System.out.println("2) 제1문헌실");
        System.out.println("3) 제2문헌실");
        System.out.println("4) 베스트셀러실");
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.print("등록 도서의 \"위치\"를 입력하세요 > ");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input)) {
                    menu = Integer.parseInt(process_input);

                    if (menu == 1)
                        return "어린이 자료실";
                    else if (menu == 2)
                        return "제1문헌실";
                    else if (menu == 3)
                        return "제2문헌실";
                    else if (menu == 4)
                        return "베스트셀러실";
                    else {
                        continue;
                    }
                } else {
                    continue;
                }
            } catch (Exception e) {
                continue;
            }
        }
    }
    private int getIndex(String title, String author){
        int idx;
        int max = 0;
        for(int i=0; i < booklist.size(); i++){
            if(title.equals(booklist.get(i).getTitle())
                    && author.equals(booklist.get(i).getAuthor())){
                if(max < booklist.get(i).getIndex()){
                    max = booklist.get(i).getIndex();
                }
            }
        }
        idx = max + 1;
        return idx;
    }
    private boolean isValid_MenuInput(String e) {
        if (e.length() != 1) {
            e = e.trim();
            if (e.length() != 1)
                return false;
            else {
                int analysis = Integer.parseInt(e);
                if (analysis < 1 || analysis > 4)
                    return false;
                else
                    this.process_input = Integer.toString(analysis);
                return true;
            }
        }
        int analysis = Integer.parseInt(e);
        return analysis >= 1 && analysis <= 4;
    }
}
