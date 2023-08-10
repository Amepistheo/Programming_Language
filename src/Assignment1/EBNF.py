from queue import Queue
from collections import deque

is_bexpr = False
error_flag = False
input_queue = Queue()

def error():
    print("syntax error!!")
    global error_flag
    error_flag= True
    while not input_queue.empty():
        input_queue.get()

def token_parse(user_input):
    i = 0
    cnt = 0
    global error_flag
    if user_input is None or len(user_input) == 0:
        # str이 None이거나 빈 문자열인 경우 예외 처리
        return
    try:
        while i < len(user_input):
            strV = user_input[i]

            if error_flag:
                return

            if strV == " ":
                pass
            elif strV.isdigit():
                num = int(strV)

                for j in range(i+1, len(user_input)):
                    strV2 = user_input[j]

                    if strV2.isdigit():
                        num = num * 10 + int(strV2)
                    else:
                        i = j-1
                        break

                input_queue.put(str(num))
            elif strV in ["*", "/", "+", "-", "(", ")"]:
                if (strV == "("):
                    cnt += 1
                elif (strV == ")") :
                    cnt -= 1
                    if (cnt < 0):
                        error()
                input_queue.put(strV)
            elif strV in ["=", "!", ">", "<"]:
                global is_bexpr
                is_bexpr = True
                input_str = strV

                if i != len(user_input)-1 and user_input[i+1] == "=":
                    input_str += "="
                    i += 1

                input_queue.put(input_str)
            else:
                error()
            i += 1
    except:
        error()

def number() :
    num = int(lex())
    return num

def lex():
    if not input_queue.empty():
        return input_queue.get()
    else:
        return ""

def nextValue():
    if not input_queue.empty():
        return input_queue.queue[0]
    else:
        return ""

def expr():
    try:
        if is_bexpr:
            return bexpr()
        else:
            return aexpr()
    except IndexError:
        return

def bexpr():
    global error_flag
    left = aexpr()
    reloap = lex()
    right = aexpr()

    # 참이면 1 아니면 0을 return
    if reloap == "==":
        return True if left == right else False
    elif reloap == "!=":
        return True if left != right else False
    elif reloap == "<":
        return True if left < right else False
    elif reloap == ">":
        return True if left > right else False
    elif reloap == "<=":
        return True if left <= right else False
    elif reloap == ">=":
        return True if left >= right else False
    else:
        error()
        return -1

def aexpr():
    data = term()

    while nextValue() == "*" or nextValue() == "/":
        oper = lex()[0]

        if oper == "*":
            data *= term()
        elif oper == "/":
            data //= term()

    return data

def term():
    data = factor()

    while nextValue() == "+" or nextValue() == "-":
        oper = lex()[0]

        if oper == "+":
            data += factor()
        elif oper == "-":
            data -= factor()

    return data

def factor():
    data = 0
    global error_flag
    # if nextValue is digit
    if nextValue()[0].isdigit():
        data = number()
    # ( <expr> )
    elif nextValue() == "(":
            lex()
            data = aexpr()
            if nextValue() == ")":
                lex()
            else:
                error()
    else:
        error()
    return data

while True:
        # 새로운 input Queue 생성
        input_queue = Queue()
        
        # input queue가 비어있지 않으면 모두 제거
        while not input_queue.empty():
            input_queue.get()
        answer = 0

        print(">> ", end="")
        user_input = input()

        if user_input == "":
            break

        # 토큰을 잘라서 input Queue에 삽입
        token_parse(user_input)

        
        # 에러 발생 시 해당 문장 스킵
        if error_flag:
            error_flag = False
            continue

        answer = expr()

        # 에러 발생 시 해당 문장 스킵
        if error_flag:
            
            error_flag = False
            continue

        if is_bexpr:
            if answer == 1:
                print("true")
            elif answer == 0:
                print("false")
            is_bexpr = False
        elif not input_queue.empty():
            error()
            continue
        else:
            print(answer)
