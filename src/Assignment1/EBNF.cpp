#include <iostream>
#include <string>
#include <cstring>
#include <queue>
using namespace std;

queue<string> input;
static bool isBexpr = false;
static bool errorFlag = false;

/* 		Prototype		*/
void error();
void tokenParse(string);
void queueClear();
int aexpr();
int term();
int factor();
int number();
string lex();
string nextValue();
int main();
string reloap;

/*------------------ - EBNF----------------------
<expr> → <bexpr> | <aexpr>
<bexpr> → <aexpr> <relop> <aexpr>
<relop> → == | != | < | > | <= | >=
<aexpr> → <term> {*<term> | / <term>}
<term> → <factor> {+<factor> | -<factor>}
<factor> → <number> | (<aexpr>)
< number > → <dec> {<dec>}
<dec> → 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
---------------------------------------------- -
*/

void error() {
	cout << "syntax error!!\n";
	errorFlag = true;
	queueClear();
}

void tokenParse(string str) {
	int cnt = 0;
	if (str.empty() || str.length() == 0) {
// str이 null이거나 빈 문자열인 경우 예외 처리
	// 예를 들어, 에러 메시지를 출력하고 함수를 종료할 수 있습니다.
	    return;
	}
	try {
      for (int i = 0; i < str.length(); i++) {
        char strV = str[i];
        if(errorFlag) return;

        if (strV == ' ');

        else if (strV >= '0' && strV <= '9') {
            int num = strV - '0';
            for (i = i+1; i < str.length(); i++) {
                char strV2 = str[i];
                if(strV2 >= '0' && strV2 <= '9')
                    num = num * 10 + (strV2 - '0');
                else{
                    i--;
                    break;
                }
            }
            input.push(to_string(num));
        }

        else if(strV == '*' || strV == '/' || strV == '+' ||
                strV == '-' || strV == '(' || strV == ')') {
        	if (strV == '(')
        	    cnt++;
        	else if (strV == ')') {
        	    cnt--;
        	    if (cnt < 0)
        	        error();
        	}
            input.push(string(1, strV));
        }

        else if(strV == '=' || strV == '!' || strV == '>' || strV == '<'){
            isBexpr = true;
            string inputStr = string(1, strV);

            if(i != str.length()-1 && str[i+1] == '=') {
                inputStr += '=';
                i++;
            }

            input.push(inputStr);
        } else {
            error();
        }
    }} catch (string e) {
    }
}

string isReloap() {
    char reloap1 = nextValue()[0];
    lex();
    char reloap2 = nextValue()[0];
    if (reloap2 == '=') {
        // 비교 연산자를 구성하는 두 개의 값을 문자열로 합칩니다.
        string reloap = string(1, reloap1) + reloap2;
        return reloap;
    } else {
        // ">"나 "<"일 경우에는 연산자 그대로 반환합니다.
        return string(1, reloap1);
    }
}

//return (reloap == '=' || reloap == '!' || reloap == '<' || reloap == '>');

int bexpr() {
        int left = aexpr();

        string reloap = lex();

        int right = aexpr();

        //참이면 1 아니면 0을 return
        if (reloap == "==")
                if (left == right) return 1;
                else return 0;
        else if (reloap == "!=")
                if (left != right) return 1;
                else return 0;
        else if (reloap == "<")
                if (left < right) return 1;
                else return 0;
        else if (reloap == ">")
                if (left > right) return 1;
                else return 0;
        else if (reloap == "<=")
                if (left <= right) return 1;
                else return 0;
        else if (reloap == ">=")
                if (left >= right) return 1;
                else return 0;
        else {
                error();
                return -1;
        }
}

int expr() {
	try {
	if(isBexpr)
	        return bexpr();
	    else
	        return aexpr();
	}
	catch (string  error) {
	}
}

int aexpr() {
	int data = term();

	while (nextValue() == "*" || nextValue() == "/") {
		char oper = lex()[0];

		if (oper == '*') data *= term();
		else if (oper == '/') data /= term();
	}
	return data;
}

int term() {
	int data = 0;
	data = factor();
	while (nextValue() == "+" || nextValue() == "-") {
		char oper = lex()[0];
		if (oper == '+') data += factor();
		else if (oper == '-') data -= factor();
	}
	return data;
}

int factor() {
	int data = 0;
	//if nextValue is digit
	if (nextValue()[0] >= '0' && nextValue()[0] <= '9') data = number();
	//( <expr> )
	else {
		if (nextValue() == "(") {
			lex();
			data = aexpr();
			if (nextValue() == ")") {
				lex();
			}
			else {
				error();
			}
		}
		else error();
	}
	return data;
}

int number() {
	return stoi(lex());
}

string lex() {
	if (!input.empty()) {
		string front = input.front();
		input.pop();
		return front;
	}
	else return "";
}

string nextValue() {
	if (!input.empty())	return input.front();
	else return "";
}

void queueClear() {
	while (!input.empty()) input.pop();
}

int main() {
	string str;
	while (true) {
	// 새로운 input Queue 생성
			int answer = 0;
			queueClear();
	        queue<string> input = queue<string>();
	        cout << ">> ";
	        getline(cin, str);
	        if (str.empty()) {
	            break;
	        }
	        tokenParse(str);
	        // 에러 발생시 해당 문장 스킵
	        if (errorFlag) {
	            errorFlag = false;
	            continue;
	        }
	        answer = expr();
	        // 에러 발생시 해당 문장 스킵
	        if (errorFlag) {
	            errorFlag = false;
	            continue;
	        }
	        if (isBexpr) {
	            if (answer == 1) {
	                cout << "true" << endl;
	            }
	            else if (answer == 0) {
	                cout << "false" << endl;
	            }

	            isBexpr = false;
	        }
	        else if (!input.empty()) {
	        	  error();
	        	  continue;
	        }
	        else {
	        		cout << answer << endl;
	        }
	    }

	    return 0;
}
