# Bucket Sort Program
# This is not an in-place algorithm
# Uses Insertion Sort to sort the bukcets
# Runs in linear time: O(n)
# Stable sort as underlying insertion sort is stable
# This bucket sort has a few modifications
# Bucket Sort asks for values between 0 & 1. To handle values outside the range
# normaziation is needed

from insertionsort import insertsort

def normalizearray(b):
    maxval = max(b)
    minval = min(b)
    for i in range(len(b)):
        b[i] = (b[i] - minval) / (maxval - minval)        

def bucketsort(b):
    arr_size = len(b)
    bucket = [[] for container in range(arr_size)]
    for i in range(arr_size):
        index = int(b[i] * arr_size)
        if index != arr_size:
            bucket[index].append(b[i])
        else:
            bucket[index - 1].append(b[i])
    for j in range(arr_size):
        insertsort(bucket[j])    
    sorted = []   
    for i in range(arr_size):
        sorted = sorted + bucket[i]
    return sorted    
        

if __name__ == "__main__":
    print("Please enter an array you wish to sort: ")
    a = [float(x) for x in input().split()]
    b = a.copy()
    normalizearray(b)
    mapper = {}
    for i in range(len(b)):
        mapper[b[i]] = a[i]
    bucket = bucketsort(b)
    for i in range(len(a)):
        print(mapper[bucket[i]])
    