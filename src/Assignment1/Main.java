package Assignment1;

import java.util.*;
import java.io.*;

public class Main {
    static Queue<String> input;
    //bexpr이면 true 아니면 false
    static boolean isBexpr = false;
    //error가 발생했으면 true
    static boolean errorFlag = false;

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            //새로운 input Queue 생성
            while (!input.isEmpty()) input.poll();
            input = new LinkedList<>();

            int answer;

            System.out.print(">> ");
            String str = br.readLine();

            if(str.equals(""))
                break;

            //토큰을 잘라서 input Queue에 삽입
            tokenParse(str);

            //에러 발생시 해당 문장 스킵
            if(errorFlag){
                errorFlag = false;
                continue;
            }

            answer = expr();

            //에러 발생시 해당 문장 스킵
            if(errorFlag){
                errorFlag = false;
                continue;
            }

            if(isBexpr){
                if(answer == 1) System.out.println("true");
                else if(answer == 0) System.out.println("false");

                isBexpr = false;
            }
            /*else if (!input.empty()) {
	        	  error();
	        	  continue;
	        }*/
            else
                System.out.println(answer);
        }
    }

    static void error(){
        System.out.println("syntax error!!");
        errorFlag = true;
        //while (!input.empty()) input.pop();
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

    //input Queue에 있는 내용을 lex()한 후 정수로 바꾸어 return
    static int number(){
        return Integer.parseInt(lex());
    }

    //String을 입력받아 token으로 나누어 input Queue에 삽입
    static void tokenParse(String str) {
        for (int i = 0; i < str.length(); i++) {
            //i번 인덱스의 문자를 strV에 저장
            char strV = str.charAt(i);

            //만약 tokenParse 중에 error가 발생했다면 return
            if(errorFlag)
                return;

            //i번 인덱스의 문자가 ' '의 경우 다음 문자 확인(공백 제거)
            if (strV == ' ') ;

                //숫자가 입력으로 왔을 경우
            else if (strV >= '0' && strV <= '9') {
                int num = strV - '0';

                //수가 한자리가 아닐 경우 계속 입력을 받는 부분
                for (i = i+1; i < str.length(); i++) {
                    char strV2 = str.charAt(i);

                    if(strV2 >= '0' && strV2 <= '9')
                        num = num * 10 + (strV2 - '0');
                    else{
                        i--;
                        break;
                    }
                }

                //input Queue에 String 형식으로 num 삽입
                input.offer(String.valueOf(num));
            }

            //입력으로 *, /, +, -, (, )가 오는 경우
            else if(strV == '*' || strV == '/' || strV == '+' ||
                    strV == '-' || strV == '(' || strV == ')'){

                input.offer(String.valueOf(strV));
            }

            //입력으로 =, !, > ,<가 오는 경우
            //이 경우에는 문장 전체가 bexpr가 변환되어야 함.
            //나중에 시간 남으면 =! 이런식으로 입력 들어오는 거 다 error 처리하기
            else if(strV == '=' || strV == '!' || strV == '>' || strV == '<'){
                isBexpr = true;
                String inputStr = String.valueOf(strV);

                //다음 문자가 =인지 아닌지 확인
                //i가 str.length()-1이면 입력의 마지막 글자이기 때문에 확인하지 않음
                if(i != str.length()-1 && str.charAt(i+1) == '='){
                    inputStr += '=';
                    i++;
                }

                input.offer(inputStr);
            }
            else
                error();
        }
    }


    // <expr> → <bexpr> | <aexpr>
    static int expr(){
        if(isBexpr)
            return bexpr();
        else
            return aexpr();
    }

    //<aexpr> → <term> {* <term> | / <term>}
    static int aexpr(){
        int data = term();

        while(nextValue().equals("*") || nextValue().equals("/")){
            //연산자를 input에서 pop해서 꺼내줌
            //어차피 *이나 /이기 때문에 문자가 하나여서 charAt(0) 가능
            char oper = lex().charAt(0);

            if(oper == '*') data *= term();
            else if(oper == '/') data /= term();
        }

        return data;
    }

    //<term> → <factor> {+ <factor> | - <factor>}
    static int term(){
        int data;

        data = factor();
        while(nextValue().equals("+") || nextValue().equals("-")){
            char oper = lex().charAt(0);

            if(oper == '+') data += factor();
            else if(oper == '-') data -= factor();
        }

        return data;
    }

    //<factor> → <number> | (<aexpr>)
    static int factor(){
        int data = 0;

        if(nextValue().charAt(0) >='0' && nextValue().charAt(0) <= '9')
            data = number();
        else if(nextValue().equals("(")){
            lex();
            data = aexpr();

            if(nextValue().equals(")"))
                lex();
            else{
                error();
            }
        }
        else error();

        return data;
    }

    //<bexpr> → <aexpr> <relop> <aexpr>
    static int bexpr() {
        int left = aexpr();

        String reloap = lex();

        int right = aexpr();

        //참이면 1 아니면 0을 return
        switch (reloap) {
            case "==":
                if (left == right) return 1;
                else return 0;
            case "!=":
                if (left != right) return 1;
                else return 0;
            case "<":
                if (left < right) return 1;
                else return 0;
            case ">":
                if (left > right) return 1;
                else return 0;
            case "<=":
                if (left <= right) return 1;
                else return 0;
            case ">=":
                if (left >= right) return 1;
                else return 0;
            default:
                error();
                return -1;
        }
    }
}
