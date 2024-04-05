import java.util.ArrayList;
import java.util.Scanner;

public class MainProgram {

    int menu;
    private Process1 process1;
    private Process2 process2;
    private Process3 process3;
    private Process4 process4;
    private ProcessForTest processForTest; //테스트용
    private String todayDate; //프로그램 처음 시작시 입력받는 오늘 날짜
    private String process_input; //메뉴 입력값
    Scanner scanner = new Scanner(System.in);


    public MainProgram() {

        //for debug
//        processForTest = new ProcessForTest();

        input_Date();

        while (true) {
            System.out.println("> A04 LMS");
            System.out.println("1) Add Book (도서 추가)");
            System.out.println("2) Search Books (도서 검색)");
            System.out.println("3) Manage Books (도서 관리)");
            System.out.println("4) Borrow Books (도서 대여)");
            System.out.println("5) Quit (종료)");
            System.out.println("------------------------------------------------------------");
            System.out.print("> A04 LMS : menu > ");
            Scanner sc = new Scanner(System.in);

            try {
                process_input = sc.nextLine();
                if (Integer.parseInt(process_input) != 5)
                    System.out.println("------------------------------------------------------------");
                if (isValid_MenuInput(process_input)) {
                    menu = Integer.parseInt(process_input);
                    if (menu == 1)
                        process1 = new Process1(todayDate);
                    else if (menu == 2)
                        process2 = new Process2(todayDate);
                    else if (menu == 3)
                        process3 = new Process3(todayDate);
                    else if (menu == 4)
                        process4 = new Process4(todayDate);

                    if (menu == 5) {
                        System.out.println("> A04 Library Management System을 종료합니다");
                        break;
                    } else {
                        System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요");
                        System.out.println("------------------------------------------------------------");
                    }
                } else {
                    System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요");
                    System.out.println("------------------------------------------------------------");
                }
            } catch (Exception e) {
                System.out.println("잘못 입력했습니다. 범위(1~5) 안에서 다시 선택해주세요");
                System.out.println("------------------------------------------------------------");

            }

        }

    }

    private void input_Date() {

        while (true) {
            System.out.println();
            System.out.print("\"년+월+일\"을 입력하세요 > ");
            String input = scanner.nextLine();
            System.out.println("------------------------------------------------------------");
//          System.out.println("input: " + input);
            if (input != null && !input.trim().isEmpty() && input.length() <= 15) {
                input = input.trim();
                String stringForCheckDateInput = input.replaceAll("\\s+", " ");
//			System.out.println("stringForCheckDateInput: " + stringForCheckDateInput);
                if (Is_valid_date(stringForCheckDateInput)) {
                    break;
                } else {
                    System.out.println("입력 가능한 문자열이 아닙니다.");
                    System.out.println("------------------------------------------------------------");
                }
            } else {
                System.out.println("입력 가능한 문자열이 아닙니다.");
                System.out.println("------------------------------------------------------------");
            }
        }
    }

    private boolean Is_valid_date(String e) {
        try {
            String[] parts = e.split(" ");
            if (parts[0].length() != 4 || parts[1].length() > 2 || parts[2].length() > 2)
                return false;

            int year, month, day;
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);

            // 유효한 날짜인지 검사
            if (Is_valid_date2(year, month, day)) {
                if (Integer.toString(day).length() == 1 || Integer.toString(month).length() == 1) {
                    if (Integer.toString(month).length() == 1 && Integer.toString(day).length() == 1)
                        this.todayDate = year + " 0" + month + " 0" + day;
                    else if (Integer.toString(month).length() == 1)
                        this.todayDate = year + " 0" + month + " " + day;
                    else
                        this.todayDate = year + " " + month + " 0" + day;
                } else
                    this.todayDate = year + " " + month + " " + day;
                //for debug
//                System.out.println("today : " + todayDate);
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

    }

    private boolean Is_valid_date2(int year, int month, int day) {
        try {
            //System.out.println("Is_valid_date2 called");
            // 31일까지 있는 달을 ArrayList로 초기화합니다.
            ArrayList<Integer> monthsWith31Days = new ArrayList<>();
            monthsWith31Days.add(1);
            monthsWith31Days.add(3);
            monthsWith31Days.add(5);
            monthsWith31Days.add(7);
            monthsWith31Days.add(8);
            monthsWith31Days.add(10);
            monthsWith31Days.add(12);

            ArrayList<Integer> monthsWith30Days = new ArrayList<>();
            monthsWith30Days.add(4);
            monthsWith30Days.add(6);
            monthsWith30Days.add(9);
            monthsWith30Days.add(11);

            if (year <= 1901 || year >= 2038) {
                return false;
            } else {
                if (year % 4 == 0) { // 윤년
                    if (monthsWith31Days.contains(month)) {
                        return day >= 0 && day <= 31;
                    } else if (monthsWith30Days.contains(month)) {
                        return day >= 0 && day <= 30;
                    } else if (month == 2) {
                        return day >= 0 && day <= 29;
                    } else {
                        return false;
                    }
                } else { // non-윤년
                    if (monthsWith31Days.contains(month)) {
                        return day >= 0 && day <= 31;
                    } else if (monthsWith30Days.contains(month)) {
                        return day >= 0 && day <= 30;
                    } else if (month == 2) {
                        return day >= 0 && day <= 28;
                    } else {
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValid_MenuInput(String e) {
        if (e.length() != 1) {
            e = e.trim();
            if (e.length() != 1)
                return false;
            else {
                int analysis = Integer.parseInt(e);
                if (analysis < 1 || analysis > 6)
                    return false;
                else
                    this.process_input = Integer.toString(analysis);
                return true;
            }
        }
        int analysis = Integer.parseInt(e);
        return analysis >= 1 && analysis <= 6;
    }

}