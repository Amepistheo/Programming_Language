package Assignment2;

import java.util.*;
import java.io.*;

public class Main {
    static LinkedList<String> input;
    //while 문을 순회할 때 input에 다시 넣어줄 String의 List
    static LinkedList<String> returnToInput = new LinkedList<>();
    static int array[];
    //error가 발생했으면 true
    static boolean errorFlag = false;
    static boolean printUsed = false;
    static List<Integer> printValue = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        int noInputCount = 0;

        while(noInputCount<2) {
            input = new LinkedList<>();
            array = new int[26];
            errorFlag = false;

            System.out.print(">> ");
            String str = br.readLine();

            if(str.equals("")) {
                noInputCount++;
                continue;
            }

            //토큰을 잘라서 input Queue에 삽입
            tokenParse(str);

            if(errorFlag)
                continue;

            program();

            if(printUsed) {
                System.out.print(">> ");

                for (int a : printValue)
                    System.out.print(a + " ");

                System.out.println();

                printValue = new LinkedList<>();
                printUsed = false;
            }
        }
    }

    static void error(){
        System.out.println(">> Syntax Error!");
        errorFlag = true;
        while (!input.isEmpty()) input.poll();
    }

    //input Queue의 front에 있는 값을 return하는 함수
    static String nextValue(){
        if(!input.isEmpty()) return input.peek();
        else return "";
    }

    //input Queue의 front에 있는 값을 pop해서 return하는 함수
    static String lex(){
        if(!input.isEmpty()) return input.poll();
        else return "";
    }

    //String을 입력받아 token으로 나누어 input Queue에 삽입
    static void tokenParse(String str) {
        //대괄호의 개수 세기
        int cnt = 0;
        //소괄호의 개수 세기
        int smallCnt = 0;

        if (str == null || str.length() == 0) {
            // str이 null이거나 빈 문자열인 경우 예외 처리
            // 예를 들어, 에러 메시지를 출력하고 함수를 종료할 수 있습니다.
            return;
        }
        try{
            for (int i = 0; i < str.length(); i++) {
                //i번 인덱스의 문자를 strV에 저장
                char strV = str.charAt(i);

                //만약 tokenParse 중에 error가 발생했다면 return
                if (errorFlag)
                    return;

                //i번 인덱스의 문자가 ' '의 경우 다음 문자 확인(공백 제거)
                if (strV == ' ') ;
                    // 문자가 입력으로 왔을 경우
                else if (strV >= 'a' && strV <= 'z') {
                    //문자열이 입력으로 들어왔을 경우 이를 하나의 토큰으로 저장
                    String inputStr = String.valueOf(strV);

                    //문자열이 입력으로 왔을 경우
                    //print와 while을 따로 처리하는 것보다 token으로 문자열을 넣어두고
                    //추후 program()에서 확인
                    for (i = i + 1; i < str.length(); i++) {
                        char strV2 = str.charAt(i);

                        if (strV2 >= 'a' && strV2 <= 'z')
                            inputStr += strV2;
                        else {
                            i--;
                            break;
                        }
                    }

                    //input Queue에 inputStr 삽입
                    input.offer(inputStr);
                }
                //숫자가 입력으로 왔을 경우
                else if (strV >= '0' && strV <= '9') {
                    int num = strV - '0';

                    //수가 한자리가 아닐 경우 계속 입력을 받는 부분
                    for (i = i + 1; i < str.length(); i++) {
                        char strV2 = str.charAt(i);

                        if (strV2 >= '0' && strV2 <= '9')
                            num = num * 10 + (strV2 - '0');
                        else {
                            i--;
                            break;
                        }
                    }

                    //input Queue에 String 형식으로 num 삽입
                    input.offer(String.valueOf(num));
                }

                //입력으로 ;, +, -, {, }, (, )가 오는 경우
                else if (strV == ';' || strV == '+' || strV == '-'
                        || strV == '{' || strV == '}' || strV == '(' || strV == ')') {
                    if (strV == '{')
                        cnt++;
                    else if (strV == '}') {
                        cnt--;
                        if (cnt < 0)
                            error();
                    }
                    else if (strV == '(')
                        smallCnt++;
                    else if (strV == ')') {
                        smallCnt--;
                        if (smallCnt < 0)
                            error();
                    }
                    input.offer(String.valueOf(strV));
                }

                //입력으로 =, !, > ,<가 오는 경우
                else if (strV == '=' || strV == '!' || strV == '>' || strV == '<') {
                    String inputStr = String.valueOf(strV);

                    //다음 문자가 =인지 아닌지 확인
                    //i가 str.length()-1이면 입력의 마지막 글자이기 때문에 확인하지 않음
                    if (i != str.length() - 1 && str.charAt(i + 1) == '=') {
                        inputStr += '=';
                        i++;
                    }

                    input.offer(inputStr);
                } else
                    error();

            }
        }
        catch (StringIndexOutOfBoundsException e) {
            error();
        }
    }

    // <program> → {<declaration>} {<statement>}
    static void program(){
        try {
            while(nextValue().equals("int")){
                declaration();
            }

            while(nextValue().equals("print")
                    || nextValue().equals("while")
                    || ((nextValue().length()==1) && (nextValue().charAt(0) >='a' && nextValue().charAt(0) <= 'z'))){
                statement();
            }

            if(!nextValue().equals(""))
                error();
        }
        catch (StringIndexOutOfBoundsException e) {
            return;
        }
    }

    static void declaration(){
        type();
        var();
        //세미콜론 제거
        lex();
    }

    //<statement> -> <var> = <aexpr> ; |
    //               print <aexpr> ; |
    //               while (<bexpr>) '{' {<statement>} '}'
    static void statement(){
        if(errorFlag)
            return;

        //<var> = <aexpr> ;
        if (((nextValue().length() == 1) && (nextValue().charAt(0) >= 'a' && nextValue().charAt(0) <= 'z'))) {
            //array 배열의 어느 index에 넣을 것인지 결정
            returnToInput.add(nextValue());
            int arrayIdx = var();

            // = 제거
            returnToInput.add(nextValue());
            lex();

            array[arrayIdx] = aexpr();

            // ; 제거
            returnToInput.add(nextValue());
            if(!lex().equals(";"))
                error();
        } else if (nextValue().equals("print")) {
            //print 문자 제거
            returnToInput.add(nextValue());
            lex();

            int printV = aexpr();

            // ; 제거
            returnToInput.add(nextValue());
            if(!lex().equals(";")) {
                error();
                return;
            }

            printValue.add(printV);
            printUsed = true;

        } else if (nextValue().equals("while")) {
            //while 문 안의 ( bexpr )의 결과가 true이면 다시 input에 넣을 returnToInput List 생성
            returnToInput = new LinkedList<>();
            int bexprReturn = 0;

            while (true) {
                //while 문자 제거
                returnToInput.add(nextValue());
                lex();
                // ( 제거
                returnToInput.add(nextValue());
                lex();

                //bexprReturn이 1이면 true, 0이면 false
                bexprReturn = bexpr();

                if(bexprReturn == 0)
                    break;

                // ) 제거
                returnToInput.add(nextValue());
                lex();

                // { 제거
                returnToInput.add(nextValue());
                lex();

                while(true){
                    statement();

                    if(nextValue().equals("") || nextValue().equals("}"))
                        break;
                }

                // } 제거
                returnToInput.add(nextValue());
                lex();


                for(int i = 0; i<returnToInput.size(); i++)
                    input.add(i, returnToInput.get(i));
                returnToInput = new LinkedList<>();
            }
            while(!lex().equals("}")) ;
        } else
            error();
    }

    //<bexpr> → <aexpr> <relop> <aexpr>
    static int bexpr() {
        int left = aexpr();

        String reloap = lex();
        returnToInput.add(reloap);

        int right = aexpr();

        //참이면 1 아니면 0을 return
        if(reloap.equals("==")) {
            if(left == right) return 1;
            else return 0;
        }
        else if(reloap.equals("!=")){
            if(left != right) return 1;
            else return 0;
        }
        else if(reloap.equals("<")){
            if(left < right) return 1;
            else return 0;
        }
        else if(reloap.equals(">")){
            if(left > right) return 1;
            else return 0;
        }
        else if(reloap.equals("<=")){
            if(left <= right) return 1;
            else return 0;
        }
        else if(reloap.equals(">=")){
            if(left >= right) return 1;
            else return 0;
        }
        else{
            error();
            return -1;
        }
    }

    //<aexpr> → <term> {+ <term> | - <term>}
    static int aexpr(){
        int data = term();

        while(nextValue().equals("+") || nextValue().equals("-")){
            returnToInput.add(nextValue());
            char oper = lex().charAt(0);

            if(oper == '+') data += term();
            else if(oper == '-') data -= term();
        }

        return data;
    }

    //<term> → <number> | <var>
    static int term(){
        int data = 0;

        if(nextValue().charAt(0) >='0' && nextValue().charAt(0) <= '9') {
            returnToInput.add(nextValue());
            data = number();
        }
        else if(nextValue().charAt(0) >='a' && nextValue().charAt(0) <= 'z') {
            returnToInput.add(nextValue());
            data = array[var()];
        }
        else error();

        return data;
    }

    //input Queue에 있는 내용을 lex()한 후 정수로 바꾸어 return
    //<number> -> <dec> {<dec>}
    static int number(){
        return Integer.parseInt(lex());
    }

    //<type> -> int
    static void type(){
        String typeName = lex();

        if(!typeName.equals("int"))
            error();
    }

    //문자를 lex()한 후 return
    static int var() {
        char var = lex().charAt(0);

        return var-'a';
    }
}
