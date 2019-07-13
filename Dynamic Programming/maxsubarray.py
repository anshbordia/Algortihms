# Maximum Subarray Program
# Worst Case Running Time: O(nlgn) --> Using Divide and Conquer


def fmcs(a, l, m, h):
    leftsum = -1000000
    rightsum = -1000000
    tempsum = 0
    for i in range(m, l - 1, -1):
        tempsum = tempsum + a[i]
        if(tempsum > leftsum):
            leftsum = tempsum
            maxleft = i
    tempsum = 0
    for j in range(m + 1, h + 1):
        tempsum = tempsum + a[j]
        if(tempsum > rightsum):
            rightsum = tempsum
            maxright = j
    return maxleft, maxright, (leftsum + rightsum)            


def fms(a, l, h):
    if(h == l):
        return l, h, a[l]
    else:
        m = int((l + h) / 2)
        leftlow, lefthigh, leftsum = fms(a, l, m)
        rightlow, righthigh, rightsum = fms(a, m + 1, h)
        crosslow, crosshigh, crosssum = fmcs(a, l, m, h)
        if(leftsum >= rightsum and leftsum >= crosssum):
            return leftlow, lefthigh, leftsum
        elif(rightsum >= leftsum and rightsum >= crosssum):
            return rightlow, righthigh, rightsum
        else:
            return crosslow, crosshigh, crosssum

if __name__ == "__main__":
    print("Please enter an array: ")
    d = [int(x) for x in input().split()]
    a = [0] * (len(d) - 1)
    for i in range(1, len(d)):
        a[i - 1] = d[i] - d[i - 1]
    leftindex, rightindex, maxsum = fms(a, 0, len(a) - 1)
    print('Left Index:',leftindex, 'Right Index:',rightindex, 'Maxsum:',maxsum)    
        
        

