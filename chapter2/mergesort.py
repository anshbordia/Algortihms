# Merge Sort Program
# This implementation is not In Place
# Worst Case Running Time: O(nlgn)

def ms(a, p, r):
    if(p < r):
        q = int((p + (r - 1))/2)
        ms(a, p, q)
        ms(a, q + 1, r)
        merge(a, p, q, r)
                 
def merge(a, p, q, r):
    n1 = q - p + 1     
    n2 = r - q
    L = [0] * n1
    R = [0] * n2
    for i in range(0, n1):
        L[i] = (a[p + i])
    for j in range(0, n2):
        R[j] = (a[q + j + 1])   
    i = 0
    j = 0
    k = p
    while(i < n1 and j < n2):
        if(L[i] <= R[j]):
            a[k] = L[i]
            i += 1
        else:
            a[k] = R[j]
            j += 1    
        k += 1    
    
    while(i < n1):
        a[k] = L[i]
        i += 1
        k += 1
    
    while(j < n2):
        a[k] = R[j]
        j += 1
        k += 1         

if __name__ == "__main__":
    print("Please enter an array you wish to sort: ")
    a = [int(x) for x in input().split()]
    ms(a, 0, len(a) - 1)
    print("Sorted Array: ", a)