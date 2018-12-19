# Insertion Sort Program
# In Place Sorting Algorithm
# Worst Case Running time: O(n^2)

def insertsort(arr):
    for j in range(1, len(arr)):
        key = arr[j]
        i = j - 1
        while(i >= 0 and arr[i] > key):
            arr[i + 1] = arr[i]
            i = i - 1
        arr[i + 1] = key
        
    return arr        
    
if __name__ == "__main__":
    print("Please enter an array you wish to sort: ")
    a = [float(x) for x in input().split()]
    sorted_array = insertsort(a)
    print("Sorted Array: ", sorted_array)
    
