//각 process는 생성자를 통해 오늘 날짜(todayDate)를 받아온다.
//todayDate는 사용자가 입력한 날짜를 의미하며, 각 년도, 월, 일은 공백으로 구분 (EX. 2023 02 01)
//모든 Process는 BookDAO를 통해 파일에서 데이터를 읽어오고, 데이터를 파일에 쓴다.
//BookDAO의 getDataFromFiles() 메소드는 bookList.txt 파일에서 데이터를 읽어와서 ArrayList<BookVO> 형태로 반환한다.
//ArrayList<BookVO>를 통해서 각 도서에 접근하고, 도서 정보를 수정 할 수 있다.
//각 Process는 특정 작업을 마치면, BookDAO의 writeDataToFiles(ArrayList<BookVO> bookList) 메소드를 호출하여, 변경된 데이터를 txt파일에 기록한다.

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Process1 {    //도서 추가 기능
    Scanner scanner;
    private String todayDate;
    private ArrayList<BookVO> booklist;
    private BookDAO bookDAO = new BookDAO();
    private String process_input;
    private ArrayList<Location> locationlist;

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
            String tmpTitle = title;
            title = title.trim();
            if(!tmpTitle.equals(title)){
                System.out.println("------------------------------------------------------------");
                continue;
            }
            if(title.contains("\t")){
                System.out.println("------------------------------------------------------------");
                continue;
            }
            if(title.isEmpty()) {
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
            String tmpAuthor = author;
            author = author.trim();
            if(!tmpAuthor.equals(author)){
                System.out.println("------------------------------------------------------------");
                continue;
            }
            if(author.contains("\t")){
                System.out.println("------------------------------------------------------------");
                continue;
            }
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
        locationlist = bookDAO.getLocationInfoList();
        int index = 1;
        Map<Integer, String> tmpLocation = new HashMap<Integer, String>();
        System.out.println("현재 자료실 정보");
        for (Location location : locationlist) {
            tmpLocation.put(index, location.getLocationName());
            System.out.println((index++) + ") " + location.getLocationName());
        }
        int menu;
        while (true) {
            System.out.println("------------------------------------------------------------");
            System.out.print("등록 도서의 \"위치\"를 입력하세요 > ");
            scanner = new Scanner(System.in);

            try {
                process_input = scanner.nextLine();
                if (isValid_MenuInput(process_input, locationlist.size())) {
                    menu = Integer.parseInt(process_input);

                    for (Integer choose : tmpLocation.keySet()) {
                        String location = tmpLocation.get(choose);
                        if(menu == choose) {
                            return location;
                        }
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

    private boolean isValid_MenuInput(String input, int n) {
        String e = input.trim();
        if(!isValidToInteger(e)) return false;
        int analysis = Integer.parseInt(e);
        return analysis >= 1 && analysis <= n;
    }

    private boolean isValidToInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
