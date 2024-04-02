// txt파일로 부터 도서 정보를 읽어오고 기록하는 클래스

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class BookDAO {

    private File bookListFile; // bookList.txt
    private File booksFolder; // src/dataFiles/books

    public BookDAO() {
        checkDataValidation();
    }

    private void checkDataValidation() {
        String projectPath = System.getProperty("user.dir");

        if (projectPath == null) {
            System.err.println("프로젝트 폴더의 경로를 찾을 수 없습니다.");
            System.exit(1);
        }

        projectPath += "/src";

        //for debug
//        System.out.println("------------------------------------------------------------");
//        System.out.println(projectPath);

        File dataFilesFolder = new File(projectPath, "dataFiles");

        if (!dataFilesFolder.exists()) {
            System.err.println("dataFiles 폴더 없음!");
            System.exit(1);
        }

        booksFolder = new File(dataFilesFolder, "books");
        if (!booksFolder.exists()) {
            System.err.println("books 폴더 없음!");
            System.exit(1);
        }

        bookListFile = new File(dataFilesFolder, "bookList.txt");
        if (!bookListFile.exists()) {
            System.err.println("bookList.txt 없음!");
            System.exit(1);
        }

        //getDataFromFiles();
    }


    public ArrayList<BookVO> getDataFromFiles() {
        ArrayList<BookVO> bookList = new ArrayList<>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(bookListFile));
            String line;

            while ((line = reader.readLine()) != null) {
                readBookListFile(line, bookList);
            }

            reader.close();
        } catch (IOException e) {
            System.err.println("파일 읽기 오류: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("숫자 형식 변환 오류: " + e.getMessage());
            System.exit(1);
        }

        //각 도서들의 이전 대여 기록 가져오기

        for (BookVO book : bookList) {
            String filename = book.getTitle() + "(" + book.getIndex() + ").txt";

            //for debug
//            System.out.println(filename);

            File bookFile = new File(booksFolder, filename);
            if (!bookFile.exists()) {
                System.err.println(filename + " 없음!");
                System.exit(1);
            }

            try {
                BufferedReader reader = new BufferedReader(new FileReader(bookFile));
                String line;
                int count = 1;
                while ((line = reader.readLine()) != null) {
                    //for debug
//                    System.out.println(line);
                    if (count == 1) {
                        readAndCheckFirstLineOfBookFile(line, book, filename, count);
                        count++;
                    } else {
                        readRecordFromBookFile(line, book, filename, count);
                        count++;
                    }

                }

                reader.close();
            } catch (IOException e) {
                System.err.println("파일 읽기 오류: " + e.getMessage());
                System.exit(1);
            } catch (NumberFormatException e) {
                System.err.println("숫자 형식 변환 오류: " + e.getMessage());
                System.exit(1);
            }
        }

        return bookList;
    }

    private static void readRecordFromBookFile(String line, BookVO book, String fileName, int count) {
        String[] parts = line.split("/");
        String[] parts2 = parts[0].split("~");
        if (parts.length == 2) {
            BookRecord record = new BookRecord(parts2[0].trim(), parts2[1].trim(), parts[1]);
            book.addBookRecord(record);
        } else {
            System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
            System.exit(1);
        }
    }

    private static void readAndCheckFirstLineOfBookFile(String line, BookVO book, String fileName, int count) {
        String[] parts = line.split("/");
        if (parts.length == 5) {
            if (!book.getTitle().equals(parts[0].trim())
                    || !book.getAuthor().equals(parts[1].trim())
                    || !book.getAddedDate().equals(parts[2].trim())
                    || book.getIndex() != Integer.parseInt(parts[3].trim())
                    || !book.getLocation().equals(parts[4].trim())) {
                System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                System.exit(1);
            }

        } else if (parts.length == 7) {
            String[] parts2 = parts[5].split("~");
            if (!book.getTitle().equals(parts[0].trim())
                    || !book.getAuthor().equals(parts[1].trim())
                    || !book.getAddedDate().equals(parts[2].trim())
                    || book.getIndex() != Integer.parseInt(parts[3].trim())
                    || !book.getLocation().equals(parts[4].trim())
                    || !book.getCurrentRecord().getStartDate().equals(parts2[0].trim())
                    || !book.getCurrentRecord().getEndDate().equals(parts2[1].trim())
                    || !book.getCurrentRecord().getStudentNum().equals(parts[6].trim())
            ) {
                System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
                System.exit(1);
            }

        } else {
            System.err.println("잘못된 형식의 데이터입니다\n" + fileName + "\n" + line + "\nin Line " + count);
            System.exit(1);
        }
    }

    private static void readBookListFile(String line, ArrayList<BookVO> bookList) {
        String[] parts = line.split("/");
        if (parts.length == 5) {
            String title = parts[0].trim();
            String author = parts[1].trim();
            String addedDate = parts[2].trim();
            int index = Integer.parseInt(parts[3].trim());
            String location = parts[4].trim();

            BookVO book = new BookVO(title, author, addedDate, index, location);
            bookList.add(book);
        } else if (parts.length == 7) {
            String title = parts[0].trim();
            String author = parts[1].trim();
            String addedDate = parts[2].trim();
            int index = Integer.parseInt(parts[3].trim());
            String location = parts[4].trim();
            String[] parts2 = parts[5].split("~");
            String startDate = parts2[0].trim();
            String endDate = parts2[1].trim();
            String studentNum = parts[6].trim();
            BookRecord record = new BookRecord(startDate, endDate, studentNum);

            BookVO book = new BookVO(title, author, addedDate, index, location, record);
            bookList.add(book);
        } else {
            System.err.println("잘못된 형식의 데이터입니다: bookList.txt " + line);
            System.exit(1);
        }
    }

    public void writeDataToFiles(ArrayList<BookVO> bookList) {

    }

}
