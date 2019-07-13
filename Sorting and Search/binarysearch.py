# Binary Search Program
# Returns position in array of element found. Returns -1 if element not present
# Worst Case Running Time: O(lgn)
# Uses insertion sort written in this folder

from insertionsort import insertsort

def bsearch(a, p, r, x):
    if(r >= p):
        mid = int((p + r)/2)
        if(x == a[mid]):
            return mid
        elif(x < a[mid]):
            return(bsearch(a, p, mid - 1, x))
            
        else:
            return(bsearch(a, mid + 1, r, x))
    return -1

if __name__ == "__main__":
    print("Please enter an array: ")
    a = [int(x) for x in input().split()]
    insertsort(a)
    print("Sorted Array: ", a)
    x = int(input("Enter number you want to search: "))
    position = bsearch(a, 0, len(a) - 1, x)
    print(position if position >= 0 else 'Not Found')